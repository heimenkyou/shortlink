package cn.luowb.shortlink.common.biz.user;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.USER_INFO_KEY;

/**
 * 根据请求头中的用户名恢复当前请求的用户上下文。
 */
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_HEADER = "X-Username";
    private static final long USER_CACHE_TIMEOUT_MINUTES = 30L;

    private final UserInfoQueryService userInfoQueryService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = request.getHeader(USER_HEADER);
        if (!StringUtils.hasText(username)) {
            return true;
        }

        String cacheKey = USER_INFO_KEY.getKey(username);
        String cachedUser = stringRedisTemplate.opsForValue().get(cacheKey);
        UserInfoDTO userInfoDTO = StringUtils.hasText(cachedUser)
                ? JSON.parseObject(cachedUser, UserInfoDTO.class)
                : loadAndCacheUser(username, cacheKey);
        UserContext.setUser(userInfoDTO);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.removeUser();
    }

    private UserInfoDTO loadAndCacheUser(String username, String cacheKey) {
        UserInfoDTO userInfoDTO = userInfoQueryService.loadUser(username);
        if (userInfoDTO == null) {
            return null;
        }
        stringRedisTemplate.opsForValue().set(
                cacheKey,
                JSON.toJSONString(userInfoDTO),
                USER_CACHE_TIMEOUT_MINUTES,
                TimeUnit.MINUTES
        );
        return userInfoDTO;
    }
}
