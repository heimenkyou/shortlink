package cn.luowb.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.luowb.shortlink.common.biz.user.UserContext;
import cn.luowb.shortlink.admin.dao.entity.GroupDO;
import cn.luowb.shortlink.admin.dao.mapper.GroupMapper;
import cn.luowb.shortlink.admin.remote.dto.LinkRemoteService;
import cn.luowb.shortlink.admin.remote.dto.req.TrashLinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.admin.service.TrashService;
import cn.luowb.shortlink.common.convention.ServiceException;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.dto.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements TrashService {
    private final GroupMapper groupMapper;
    private final LinkRemoteService linkRemoteService;

    /**
     * 根据当前用户拥有的分组，分页查询回收站链接
     *
     * @param requestParam 分页查询请求参数
     * @return 分页查询结果
     */
    @Override
    public Result<PageResult<LinkPageRespDTO>> pageTrashLink(TrashLinkPageReqDTO requestParam) {
        // 查询当前用户拥有的分组标识列表
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .select(GroupDO::getGid);
        List<String> gidList = groupMapper.selectObjs(wrapper);
        if (CollUtil.isEmpty(gidList)) {
            throw new ServiceException("当前用户没有分组");
        }
        requestParam.setGidList(gidList);
        return linkRemoteService.pageTrashLink(requestParam);
    }
}
