package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.luowb.shortlink.common.convention.ServiceException;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.service.LinkService;
import cn.luowb.shortlink.project.util.HashUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 短链接服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

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
                .eq(LinkDO::getDelFlag, 0);

        IPage<LinkDO> resultPage = this.page(page, wrapper);

        return PageResult.of(resultPage.convert(each -> BeanUtil.toBean(each, LinkPageRespDTO.class)));
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