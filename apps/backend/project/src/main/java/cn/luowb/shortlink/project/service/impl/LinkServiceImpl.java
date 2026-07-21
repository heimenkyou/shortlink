package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.luowb.shortlink.common.convention.ServiceException;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.entity.LinkGotoDO;
import cn.luowb.shortlink.project.dao.mapper.LinkGotoMapper;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import cn.luowb.shortlink.project.mq.producer.LinkStatsMessageProducer;
import cn.luowb.shortlink.project.service.LinkService;
import cn.luowb.shortlink.project.service.OriginUrlWhitelistService;
import cn.luowb.shortlink.project.service.UrlMetadataService;
import cn.luowb.shortlink.project.util.HashUtil;
import cn.luowb.shortlink.project.util.LinkUtil;
import cn.luowb.shortlink.project.util.UserAgentExtractor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.GOTO_SHORT_LINK_KEY;
import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.LOCK_GOTO_SHORT_LINK_KEY;

/**
 * 短链接服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final LinkMapper linkMapper;
    private final LinkGotoMapper linkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final OriginUrlWhitelistService originUrlWhitelistService;
    private final UrlMetadataService urlMetadataService;
    private final LinkStatsMessageProducer linkStatsMessageProducer;


    /**
     * 解析短链接并统计访问数据
     *
     * @param shortUrl 短链接后缀
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 原始链接
     */
    @Override
    public String resolveShortUrl(String shortUrl, HttpServletRequest request, HttpServletResponse response) {
        String domain = request.getServerName();
        String fullShortUrl = domain + "/" + shortUrl;
        // 解析短链接
        String originUrl = resolve(fullShortUrl, request);
        // 统计数据
        recordStats(fullShortUrl, request, response);
        return originUrl;
    }

    /**
     * 解析短链接
     *
     * @param fullShortUrl 完整短链接
     * @param request      HTTP请求
     * @return 原始链接
     */
    private String resolve(String fullShortUrl, HttpServletRequest request) {
        // 在 redis 中查缓存，快速返回大部分正常请求
        String cacheKey = GOTO_SHORT_LINK_KEY.getKey(fullShortUrl);
        String cachedUrl = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl != null) {
            if (cachedUrl.isEmpty()) {
                throw new ClientException("短链接不存在");
            }
            return cachedUrl;
        }
        // 布隆过滤器判空，拦截大部分恶意请求
        if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            throw new ClientException("短链接不存在");
        }
        // 准备重建缓存
        RLock lock = redissonClient.getLock(LOCK_GOTO_SHORT_LINK_KEY.getKey(fullShortUrl));
        String originUrl;
        lock.lock();
        try {
            // 再次查缓存，
            cachedUrl = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedUrl != null) {
                if (cachedUrl.isEmpty()) {
                    throw new ClientException("短链接不存在");
                }
                return cachedUrl;
            }
            // 过期时间设置为 10 分钟 + 随机数（0-60 秒）
            int cacheTime = 10 * 60 + (int) (Math.random() * 60);
            // 先查路由表获取 gid
            LinkGotoDO linkGotoDO = linkGotoMapper.selectOne(Wrappers.lambdaQuery(LinkGotoDO.class)
                    .eq(LinkGotoDO::getFullShortUrl, fullShortUrl));
            if (linkGotoDO == null) {
                // 缓存空值，用于布隆过滤器误判的情况
                stringRedisTemplate.opsForValue().set(cacheKey, "", 30, TimeUnit.SECONDS);
                throw new ClientException("短链接不存在");
            }
            // 再根据 gid 查原始链接
            String gid = linkGotoDO.getGid();
            LinkDO linkDO = this.getOne(Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, gid)
                    .eq(LinkDO::getFullShortUrl, fullShortUrl)
                    .eq(LinkDO::getEnableStatus, 0)
                    .eq(LinkDO::getDelFlag, 0)
            );
            // 判断是否过期
            if (linkDO == null || (linkDO.getValidDate() != null && linkDO.getValidDate().isBefore(LocalDateTime.now()))) {
                stringRedisTemplate.opsForValue().set(cacheKey, "", 30, TimeUnit.SECONDS);
                throw new ClientException("短链接不存在或已过期");
            }
            originUrl = linkDO.getOriginUrl();
            // 正常缓存
            stringRedisTemplate.opsForValue().set(cacheKey, originUrl, cacheTime, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
        return originUrl;
    }

    /**
     * 统计访问数据
     */
    private void recordStats(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        String uvFlag = getOrCreateUvFlag(request, response);
        LinkStatsRecordMessage message = LinkStatsRecordMessage.builder()
                .messageId(IdUtil.fastSimpleUUID())
                .fullShortUrl(fullShortUrl)
                .uvFlag(uvFlag)
                .ip(JakartaServletUtil.getClientIP(request))
                .os(UserAgentExtractor.extractOs(request))
                .browser(UserAgentExtractor.extractBrowser(request))
                .device(UserAgentExtractor.extractDevice(request))
                .network(UserAgentExtractor.estimateNetwork(request))
                .occurredAt(LocalDateTime.now())
                .build();
        linkStatsMessageProducer.send(message);
    }

    /**
     * 获取或生成 UV 标识
     */
    private String getOrCreateUvFlag(HttpServletRequest request, HttpServletResponse response) {
        String uvFlag = null;
        Cookie[] cookies = request.getCookies();
        if (ArrayUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if ("uvFlag".equals(cookie.getName())) {
                    uvFlag = cookie.getValue();
                    // 说明是老用户了
                    break;
                }
            }
        }
        if (StrUtil.isBlank(uvFlag)) {
            uvFlag = IdUtil.fastSimpleUUID();
            Cookie uvFlagCookie = new Cookie("uvFlag", uvFlag);
            uvFlagCookie.setMaxAge(60 * 60 * 24 * 30); // 30 天
            uvFlagCookie.setPath("/");
            response.addCookie(uvFlagCookie);
        }
        return uvFlag;
    }

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 创建短链接响应参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public LinkCreateRespDTO createShortLink(LinkCreateReqDTO requestParam) {
        originUrlWhitelistService.validate(requestParam.getOriginUrl());
        String suffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + suffix;
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setFullShortUrl(fullShortUrl);
        linkDO.setShortUri(suffix);
        linkDO.setEnableStatus(0);
        linkDO.setTotalPv(0);
        linkDO.setTotalUv(0);
        linkDO.setTotalUip(0);
        // 获取网站图标
        WebsiteMetadataRespDTO metadata = urlMetadataService.fetchMetadata(requestParam.getOriginUrl());
        linkDO.setFavicon(metadata.getFavicon());
        try {
            // 插入短链接记录
            this.save(linkDO);
            // 插入跳转记录
            LinkGotoDO linkGotoDO = LinkGotoDO.builder()
                    .gid(requestParam.getGid())
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkGotoMapper.insert(linkGotoDO);
        } catch (DuplicateKeyException e) {
            log.warn("短链接重复入库，{}", suffix);
            throw new ServiceException("短链接生成冲突，请稍后重试");
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        // 删除在redis中可能存在的空值缓存
        stringRedisTemplate.delete(GOTO_SHORT_LINK_KEY.getKey(fullShortUrl));
        // 缓存预热
        String cacheKey = GOTO_SHORT_LINK_KEY.getKey(fullShortUrl);
        long cacheTime = LinkUtil.getCacheTime(requestParam.getValidDate());
        stringRedisTemplate.opsForValue().set(cacheKey, requestParam.getOriginUrl(), cacheTime, TimeUnit.MILLISECONDS);
        return BeanUtil.toBean(linkDO, LinkCreateRespDTO.class);
    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 分页查询短链接结果集
     */
    @Override
    public PageResult<LinkPageRespDTO> pageShortLink(LinkPageReqDTO requestParam) {
        IPage<LinkDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());

        LambdaQueryWrapper<LinkDO> wrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getEnableStatus, 0)
                .eq(LinkDO::getDelFlag, 0)
                .orderByDesc(LinkDO::getCreateTime);

        IPage<LinkDO> resultPage = this.page(page, wrapper);

        return PageResult.of(resultPage.convert(each -> BeanUtil.toBean(each, LinkPageRespDTO.class)));
    }

    /**
     * 查询短链接分组下的短链接数量
     *
     * @param gidList 分组ID列表
     * @return 分组ID列表对应的 分组数量列表
     */
    @Override
    public List<GroupCountQueryRespDTO> groupShortLinkCount(List<String> gidList) {
        return linkMapper.selectCountByGid(gidList);
    }

    /**
     * 修改短链接
     *
     * @param requestParam 修改短链接请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(LinkUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<LinkDO> wrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(LinkDO::getGid, requestParam.getOldGid())
                .eq(LinkDO::getEnableStatus, 0)
                .eq(LinkDO::getDelFlag, 0);
        // 查询短链接是否存在
        LinkDO linkDO = linkMapper.selectOne(wrapper);
        if (linkDO == null) {
            throw new ServiceException("短链接不存在");
        }
        // 将前端传来的修改属性覆盖到旧对象上
        BeanUtil.copyProperties(requestParam, linkDO);
        linkDO.setGid(requestParam.getNewGid());
        // 清空更新时间和主键，确保自动生成
        linkDO.setUpdateTime(null);
        linkDO.setId(null);

        // 如果没改 gid，直接更新
        if (requestParam.getOldGid().equals(requestParam.getNewGid())) {
            if (!this.update(linkDO, wrapper)) {
                throw new ServiceException("短链接更新失败");
            }
        } else {
            // 如果改了 gid，先删后增
            if (!this.remove(wrapper)) {
                throw new ServiceException("短链接更新失败");
            }
            this.save(linkDO);
        }
        // 删除在redis中可能存在的缓存
        stringRedisTemplate.delete(GOTO_SHORT_LINK_KEY.getKey(requestParam.getFullShortUrl()));
    }


    /**
     * 生成短链接后缀
     *
     * @return 短链接后缀
     */
    private String generateSuffix(LinkCreateReqDTO requestParam) {
        int generateCount = 0;
        String shortUri;
        do {
            if (generateCount > 10) {
                throw new ServiceException("短链接生成频繁，请稍后重试");
            }
            String originUrl = requestParam.getOriginUrl();
            shortUri = HashUtil.hashToBase62(originUrl + IdUtil.fastSimpleUUID());
            generateCount++;
        } while (shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUri));
        return shortUri;
    }
}
