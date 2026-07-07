package cn.luowb.shortlink.admin.service;

import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dto.resp.UserRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);
}
