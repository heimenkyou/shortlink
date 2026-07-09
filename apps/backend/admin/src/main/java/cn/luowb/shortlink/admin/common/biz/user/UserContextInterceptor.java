package cn.luowb.shortlink.admin.common.biz.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.dao.mapper.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String username = StpUtil.getLoginIdAsString();
        UserDO userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username));
        if (userDO != null) {
            UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                    .id(userDO.getId())
                    .username(userDO.getUsername())
                    .realName(userDO.getRealName())
                    .build();
            UserContext.setUser(userInfoDTO);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}