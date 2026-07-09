package cn.luowb.shortlink.admin.common.biz.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.luowb.shortlink.admin.dao.entity.UserDO;
import cn.luowb.shortlink.admin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        Long userId = StpUtil.getLoginIdAsLong();

        UserDO userDO = userService.getById(userId);
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