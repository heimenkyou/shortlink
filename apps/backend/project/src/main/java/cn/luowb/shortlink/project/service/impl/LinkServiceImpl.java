package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.service.LinkService;
import cn.luowb.shortlink.project.util.HashUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 短链接服务实现类
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {

    @Override
    public LinkCreateRespDTO createShortLink(LinkCreateReqDTO requestParam) {
        String suffix = generateSuffix(requestParam);
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setFullShortUrl(requestParam.getDomain() + "/" + suffix);
        linkDO.setShortUri(suffix);
        linkDO.setEnableStatus(0);
        this.save(linkDO);
        return BeanUtil.toBean(linkDO, LinkCreateRespDTO.class);
    }

    /**
     * 生成短链接后缀
     *
     * @return 短链接后缀
     */
    private String generateSuffix(LinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        return HashUtil.hashToBase62(originUrl);
    }
}