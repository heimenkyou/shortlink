package cn.luowb.shortlink.admin.service;

import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dto.req.UserLoginReqDTO;
import cn.luowb.shortlink.admin.dto.req.UserRegisterDTO;
import cn.luowb.shortlink.admin.dto.req.UserUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.UserLoginRespDTO;
import cn.luowb.shortlink.admin.dto.resp.UserRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 用户注册
     *
     * @param requestParam 注册请求参数
     */
    void register(UserRegisterDTO requestParam);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    Boolean hasUserName(String username);

    /**
     * 根据用户名修改用户信息
     *
     * @param requestParam 修改请求参数
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 登录请求参数
     * @return 登录响应
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     *
     * @return 是否登录成功
     */
    Boolean checkLogin(String token);

    /**
     * 用户退出登录
     *
     * @param token 登录凭证
     */
    void logout(String token);
}