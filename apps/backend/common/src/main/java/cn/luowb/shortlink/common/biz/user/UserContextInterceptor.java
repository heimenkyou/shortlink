package cn.luowb.shortlink.common.biz.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 根据网关透传的可信请求头恢复用户上下文。
 */
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String REAL_NAME_HEADER = "X-Real-Name";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader(USER_ID_HEADER);
        String username = request.getHeader(USERNAME_HEADER);
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(username)) {
            return true;
        }
        String realName = request.getHeader(REAL_NAME_HEADER);
        UserContext.setUser(UserInfoDTO.builder()
                .id(Long.valueOf(userId))
                .username(UriUtils.decode(username, StandardCharsets.UTF_8))
                .realName(StringUtils.hasText(realName) ? UriUtils.decode(realName, StandardCharsets.UTF_8) : null)
                .build());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.removeUser();
    }
}
