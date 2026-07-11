package cn.luowb.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.luowb.shortlink.admin.common.biz.user.UserContext;
import cn.luowb.shortlink.admin.dao.entity.GroupDO;
import cn.luowb.shortlink.admin.dao.mapper.GroupMapper;
import cn.luowb.shortlink.admin.dto.req.GroupSortReqDTO;
import cn.luowb.shortlink.admin.dto.req.GroupUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.GroupRespDTO;
import cn.luowb.shortlink.admin.remote.dto.LinkRemoteService;
import cn.luowb.shortlink.admin.remote.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.admin.service.GroupService;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import cn.luowb.shortlink.common.convention.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 短链接分组服务实现
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    // TODO 以后再改成 feign 调用
    LinkRemoteService linkRemoteService = new LinkRemoteService() {
    };

    /**
     * 新增短链接分组
     */
    @Override
    public void save(String groupName) {
        String username = UserContext.getUsername();
        save(username, groupName);
    }


    @Override
    public void save(String username, String groupName) {
        // 生成不重复的分组标识
        String gid;
        while (true) {
            gid = RandomUtil.randomNumbers(6);
            LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getGid, gid)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            if (!this.exists(wrapper)) {
                break;
            }
        }
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .username(username)
                .sortOrder(0)
                .build();
        if (!this.save(groupDO)) {
            throw new ClientException("分组创建失败");
        }
    }

    /**
     * 查询当前用户的所有短链接分组
     */
    @Override
    public List<GroupRespDTO> listGroup() {
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = this.list(wrapper);
        // 1. 拷贝分组基础信息
        List<GroupRespDTO> result = BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
        // 2. 查询每个分组下的短链接数量
        Result<List<GroupCountQueryRespDTO>> countResult = linkRemoteService.groupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
        if (!countResult.isSuccess()) {
            throw new ClientException("分组短链接数量查询失败");
        }
        // 3. 构建 gid -> 短链接数量 的映射
        List<GroupCountQueryRespDTO> countList = countResult.getData();
        Map<String, Integer> countMap = countList.stream()
                .collect(Collectors.toMap(GroupCountQueryRespDTO::getGid,
                        GroupCountQueryRespDTO::getShortLinkCount));
        // 4. 填充每个分组的短链接数量（若无则默认0）
        for (GroupRespDTO respDTO : result) {
            Integer count = countMap.getOrDefault(respDTO.getGid(), 0);
            respDTO.setShortLinkCount(count);
        }
        return result;
    }

    /**
     * 修改短链接分组
     */
    @Override
    public void update(GroupUpdateReqDTO requestParam) {
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = BeanUtil.toBean(requestParam, GroupDO.class);
        if (!this.update(groupDO, wrapper)) {
            throw new ClientException("分组更新失败");
        }
    }

    /**
     * 删除短链接分组
     */
    @Override
    public void delete(String gid) {
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = this.getOne(wrapper);
        if (groupDO == null) {
            throw new ClientException("分组不存在");
        }
        groupDO.setDelFlag(1);
        if (!this.updateById(groupDO)) {
            throw new ClientException("分组删除失败");
        }
    }

    @Override
    public void sort(List<GroupSortReqDTO> requestParam) {
        requestParam.forEach(sortReqDTO -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(sortReqDTO.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getGid, sortReqDTO.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            this.update(groupDO, wrapper);
        });
    }

}