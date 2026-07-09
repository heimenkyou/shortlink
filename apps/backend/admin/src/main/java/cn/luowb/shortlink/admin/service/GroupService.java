package cn.luowb.shortlink.admin.service;

import cn.luowb.shortlink.admin.dao.entity.GroupDO;
import cn.luowb.shortlink.admin.dto.req.GroupUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.GroupRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 短链接分组服务接口
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     *
     * @param groupName 创建请求参数
     */
    void save(String groupName);

    /**
     * 查询当前用户的所有短链接分组
     *
     * @return 短链接分组列表
     */
    List<GroupRespDTO> listGroup();

    /**
     * 修改短链接分组
     *
     * @param requestParam 修改请求参数
     */
    void update(GroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     *
     * @param gid 分组标识
     */
    void delete(String gid);
}