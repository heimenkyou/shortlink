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
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = this.getOne(wrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }
        // 直接复用同名字段拷贝，保持返回层与持久层解耦。
        return BeanUtil.copyProperties(userDO, UserRespDTO.class);
    }

    /**
     * 用户注册时直接查库，避免布隆过滤器未回灌导致误判。
     *
     * @param requestParam 注册请求参数
     */
    @Override
    public void register(UserRegisterDTO requestParam) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getDelFlag, 0);
        if (this.exists(wrapper)) {
            throw new ClientException("用户名已存在");
        }
        UserDO userDO = BeanUtil.copyProperties(requestParam, UserDO.class);
        userDO.setDelFlag(0);
        if (!this.save(userDO)) {
            throw new ServiceException("用户注册失败");
        }
        userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
    }

    @Override
    public Boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
//        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
//                .eq(UserDO::getUsername, username);
//        return this.exists(wrapper);
    }
}
