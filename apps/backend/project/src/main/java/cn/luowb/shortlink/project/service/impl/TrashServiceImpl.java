package cn.luowb.shortlink.project.service.impl;

import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.project.service.TrashService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.GOTO_SHORT_LINK_KEY;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements TrashService {
    private final StringRedisTemplate stringRedisTemplate;

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
}
