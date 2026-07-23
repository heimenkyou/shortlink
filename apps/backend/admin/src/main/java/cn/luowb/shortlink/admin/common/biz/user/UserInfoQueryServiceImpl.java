package cn.luowb.shortlink.admin.common.biz.user;

import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dao.mapper.UserMapper;
import cn.luowb.shortlink.common.biz.user.UserInfoDTO;
import cn.luowb.shortlink.common.biz.user.UserInfoQueryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 管理端用户查询适配器。
 */
@Component
@RequiredArgsConstructor
public class UserInfoQueryServiceImpl implements UserInfoQueryService {

    private final UserMapper userMapper;

    /**
     * 按用户名查询管理端用户信息。
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public UserInfoDTO loadUser(String username) {
        UserDO userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0));
        if (userDO == null) {
            return null;
        }
        return UserInfoDTO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .realName(userDO.getRealName())
                .build();
    }
}
