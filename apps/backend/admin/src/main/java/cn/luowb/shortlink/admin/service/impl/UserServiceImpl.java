package cn.luowb.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.luowb.shortlink.admin.common.convention.ServiceException;
import cn.luowb.shortlink.admin.common.convention.exception.ClientException;
import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dao.mapper.UserMapper;
import cn.luowb.shortlink.admin.dto.req.UserRegisterDTO;
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
            throw new ClientException("用户不存在");
        }
        return BeanUtil.copyProperties(userDO, UserRespDTO.class);
    }

    /**
     * 用户注册
     */
    @Override
    public void register(UserRegisterDTO requestParam) {
        if (hasUserName(requestParam.getUsername())) {
            throw new ClientException("用户名已存在");
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY.getKey(requestParam.getUsername()));
        if (!lock.tryLock()) {
            throw new ServiceException("用户名已存在");
        }
        try {
            UserDO userDO = BeanUtil.copyProperties(requestParam, UserDO.class);
            userDO.setDelFlag(0);
            if (!this.save(userDO)) {
                throw new ServiceException("用户注册失败");
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
}
