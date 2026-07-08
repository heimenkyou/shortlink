package cn.luowb.shortlink.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.luowb.shortlink.admin.common.convention.ServiceException;
import cn.luowb.shortlink.admin.common.convention.exception.ClientException;
import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dao.mapper.UserMapper;
import cn.luowb.shortlink.admin.dto.req.UserLoginReqDTO;
import cn.luowb.shortlink.admin.dto.req.UserRegisterDTO;
import cn.luowb.shortlink.admin.dto.req.UserUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.UserLoginRespDTO;
import cn.luowb.shortlink.admin.dto.resp.UserRespDTO;
import cn.luowb.shortlink.admin.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import static cn.luowb.shortlink.admin.common.constant.RedisCacheKeyEnum.LOCK_USER_REGISTER_KEY;
import static cn.luowb.shortlink.admin.common.convention.result.errorcode.BaseErrorCode.USER_NAME_EXIST_ERROR;
import static cn.luowb.shortlink.admin.common.convention.result.errorcode.BaseErrorCode.USER_REGISTER_ERROR;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

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
            userDO.setDelFlag(0);
            if (!this.save(userDO)) {
                throw new ServiceException(USER_REGISTER_ERROR);
            }
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
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
        String loginUserId = StpUtil.getLoginIdAsString();

        // 密码加密
        requestParam.setPassword(BCrypt.hashpw(requestParam.getPassword()));
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getId, loginUserId);
        if (!this.update(BeanUtil.toBean(requestParam, UserDO.class), wrapper)) {
            throw new ClientException("更新失败");
        }
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

        StpUtil.login(userDO.getId());

        String token = StpUtil.getTokenInfo().getTokenValue();
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String token) {
        return StpUtil.getLoginIdByToken(token) != null;
    }
}