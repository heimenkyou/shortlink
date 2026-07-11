package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.luowb.shortlink.common.convention.ServiceException;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.service.LinkService;
import cn.luowb.shortlink.project.util.HashUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 短链接服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final LinkMapper linkMapper;

    @Override
    public LinkCreateRespDTO createShortLink(LinkCreateReqDTO requestParam) {
        String suffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + suffix;
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setFullShortUrl(fullShortUrl);
        linkDO.setShortUri(suffix);
        linkDO.setEnableStatus(0);
        try {
            this.save(linkDO);
        } catch (DuplicateKeyException e) {
            log.warn("短链接重复入库，{}", suffix);
            throw new ServiceException("短链接生成冲突，请稍后重试");
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return BeanUtil.toBean(linkDO, LinkCreateRespDTO.class);
    }

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