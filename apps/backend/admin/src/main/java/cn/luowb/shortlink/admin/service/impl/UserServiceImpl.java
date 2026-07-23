package cn.luowb.shortlink.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.luowb.shortlink.common.biz.user.UserContext;
import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dao.mapper.UserMapper;
import cn.luowb.shortlink.admin.dto.req.UserLoginReqDTO;
import cn.luowb.shortlink.admin.dto.req.UserRegisterDTO;
import cn.luowb.shortlink.admin.dto.req.UserUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.UserLoginRespDTO;
import cn.luowb.shortlink.admin.dto.resp.UserRespDTO;
import cn.luowb.shortlink.admin.service.GroupService;
import cn.luowb.shortlink.admin.service.UserService;
import cn.luowb.shortlink.common.convention.ServiceException;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static cn.luowb.shortlink.common.biz.user.UserContext.USER_INFO_SESSION_KEY;
import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.LOCK_USER_REGISTER_KEY;
import static cn.luowb.shortlink.common.convention.result.errorcode.BaseErrorCode.USER_NAME_EXIST_ERROR;
import static cn.luowb.shortlink.common.convention.result.errorcode.BaseErrorCode.USER_REGISTER_ERROR;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final GroupService groupService;

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = this.getOne(wrapper);
        if (userDO == null) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }
        return BeanUtil.toBean(userDO, UserRespDTO.class);
    }

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public void register(UserRegisterDTO requestParam) {
        if (hasUserName(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }
        // 密码加密
        requestParam.setPassword(BCrypt.hashpw(requestParam.getPassword()));
        // 分布式锁防止重复注册
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY.getKey(requestParam.getUsername()));
        if (!lock.tryLock()) {
            throw new ServiceException(USER_NAME_EXIST_ERROR);
        }
        try {
            UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
            if (!this.save(userDO)) {
                throw new ServiceException(USER_REGISTER_ERROR);
            }
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            // 注册成功后，添加默认短链接分组
            groupService.save(userDO.getUsername(), "默认分组");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // 保证当前用户为登录用户
        Long loginUserId = UserContext.getUserId();

        // 密码加密
        requestParam.setPassword(BCrypt.hashpw(requestParam.getPassword()));
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getId, loginUserId);
        if (!this.update(BeanUtil.toBean(requestParam, UserDO.class), wrapper)) {
            throw new ClientException("更新失败");
        }
        StpUtil.getSession().set(
                USER_INFO_SESSION_KEY,
                buildSessionUserInfo(loginUserId, requestParam.getUsername(), requestParam.getRealName())
        );
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        UserDO userDO = this.getOne(wrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }
        if (!BCrypt.checkpw(requestParam.getPassword(), userDO.getPassword())) {
            throw new ClientException("密码错误");
        }

        // 用户名为分片键，所以使用用户名作为登录ID
        StpUtil.login(userDO.getUsername());
        StpUtil.getSession().set(
                USER_INFO_SESSION_KEY,
                buildSessionUserInfo(userDO.getId(), userDO.getUsername(), userDO.getRealName())
        );

        String token = StpUtil.getTokenInfo().getTokenValue();
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String token) {
        return StpUtil.getLoginIdByToken(token) != null;
    }

    @Override
    public void logout(String token) {
        Object loginUserId = StpUtil.getLoginIdByToken(token);
        if (loginUserId == null) {
            throw new ClientException("用户未登录");
        }
        StpUtil.logout(loginUserId);
    }

    private Map<String, Object> buildSessionUserInfo(Long id, String username, String realName) {
        // 使用通用结构，避免网关反序列化 Session 时依赖业务 DTO。
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", id);
        userInfo.put("username", username);
        userInfo.put("realName", realName);
        return userInfo;
    }
}
