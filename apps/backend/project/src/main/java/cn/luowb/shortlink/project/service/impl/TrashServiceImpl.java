package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.TrashDeleteReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashLinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashRecoverReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.service.TrashService;
import cn.luowb.shortlink.project.util.LinkUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.GOTO_SHORT_LINK_KEY;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements TrashService {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 将链接移动到回收站
     *
     * @param requestParam 请求参数
     */
    @Override
    public void saveTrash(TrashSaveReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> wrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getEnableStatus, 0)
                .eq(LinkDO::getDelFlag, 0);
        LinkDO linkDO = LinkDO.builder()
                .enableStatus(1)
                .build();
        this.update(linkDO, wrapper);
        // 删除缓存
        stringRedisTemplate.delete(GOTO_SHORT_LINK_KEY.getKey(requestParam.getFullShortUrl()));
    }

    /**
     * 分页查询回收站中的链接
     *
     * @param requestParam 请求参数
     * @return 分页结果
     */
    @Override
    public PageResult<LinkPageRespDTO> pageTrashLink(TrashLinkPageReqDTO requestParam) {
        IPage<LinkDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());

        LambdaQueryWrapper<LinkDO> wrapper = Wrappers.lambdaQuery(LinkDO.class)
                .in(LinkDO::getGid, requestParam.getGidList())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getDelFlag, 0)
                .orderByDesc(LinkDO::getUpdateTime);

        IPage<LinkDO> resultPage = this.page(page, wrapper);

        return PageResult.of(resultPage.convert(each -> BeanUtil.toBean(each, LinkPageRespDTO.class)));
    }

    /**
     * 从回收站中恢复链接
     *
     * @param requestParam 请求参数
     */
    @Override
    public void recoverTrash(TrashRecoverReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> wrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getDelFlag, 0);
        LinkDO linkDO = this.getOne(wrapper);
        if (linkDO == null) {
            throw new ClientException("链接不存在");
        }
        LinkDO updateDO = LinkDO.builder()
                .enableStatus(0)
                .build();
        this.update(updateDO, wrapper);
        // 删除可能的空值缓存
        stringRedisTemplate.delete(GOTO_SHORT_LINK_KEY.getKey(requestParam.getFullShortUrl()));
        // 缓存预热
        String cacheKey = GOTO_SHORT_LINK_KEY.getKey(requestParam.getFullShortUrl());
        long cacheTime = LinkUtil.getCacheTime(linkDO.getValidDate());
        stringRedisTemplate.opsForValue().set(cacheKey, linkDO.getOriginUrl(), cacheTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 从回收站中删除链接
     *
     * @param requestParam 请求参数
     */
    @Override
    public void deleteTrash(TrashDeleteReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> wrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getDelFlag, 0);
        LinkDO linkDO = this.getOne(wrapper);
        if (linkDO == null) {
            throw new ClientException("链接不存在");
        }
        LinkDO updateDO = LinkDO.builder()
                .delFlag(1)
                .build();
        this.update(updateDO, wrapper);
        // 删除可能的空值缓存
        stringRedisTemplate.delete(GOTO_SHORT_LINK_KEY.getKey(requestParam.getFullShortUrl()));
    }
}