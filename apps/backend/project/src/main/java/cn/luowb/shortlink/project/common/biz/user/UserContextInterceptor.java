package cn.luowb.shortlink.project.common.biz.user;

import cn.luowb.shortlink.project.dao.entity.UserDO;
import cn.luowb.shortlink.project.dao.mapper.UserMapper;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.USER_INFO_KEY;

@Component
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_HEADER = "X-Username";
    private static final long USER_CACHE_TIMEOUT_MINUTES = 30L;

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
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

    private UserInfoDTO loadAndCacheUser(String username, String cacheKey) {
        UserDO userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0));
        if (userDO == null) {
            return null;
        }
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .realName(userDO.getRealName())
                .build();
        stringRedisTemplate.opsForValue().set(
                cacheKey, JSON.toJSONString(userInfoDTO), USER_CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        return userInfoDTO;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.removeUser();
    }
}
