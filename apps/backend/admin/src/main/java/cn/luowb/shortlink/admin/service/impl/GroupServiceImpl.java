package cn.luowb.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.luowb.shortlink.admin.common.biz.user.UserContext;
import cn.luowb.shortlink.common.convention.exception.exception.ClientException;
import cn.luowb.shortlink.admin.dao.entity.GroupDO;
import cn.luowb.shortlink.admin.dao.mapper.GroupMapper;
import cn.luowb.shortlink.admin.dto.req.GroupSortReqDTO;
import cn.luowb.shortlink.admin.dto.req.GroupUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.GroupRespDTO;
import cn.luowb.shortlink.admin.service.GroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短链接分组服务实现
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    /**
     * 新增短链接分组
     */
    @Override
    public void save(String groupName) {
        String username = UserContext.getUsername();
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
        return BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
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