package cn.luowb.shortlink.admin.common.biz.user;

import cn.luowb.shortlink.admin.config.UserFlowRiskControlProperties;
import cn.luowb.shortlink.common.biz.user.UserContext;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.USER_FLOW_RISK_CONTROL_KEY;

/**
 * 管理端用户请求频控拦截器。
 */
@Component
@RequiredArgsConstructor
public class UserFlowRiskControlInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserFlowRiskControlProperties properties;

    private static final DefaultRedisScript<Long> USER_FLOW_RISK_CONTROL_SCRIPT = new DefaultRedisScript<>();

    static {
        USER_FLOW_RISK_CONTROL_SCRIPT.setLocation(new ClassPathResource("lua/user_flow_risk_control.lua"));
        USER_FLOW_RISK_CONTROL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = UserContext.getUsername();
        Long accessCount = stringRedisTemplate.execute(
                USER_FLOW_RISK_CONTROL_SCRIPT,
                Collections.singletonList(USER_FLOW_RISK_CONTROL_KEY.getKey(username)),
                String.valueOf(properties.getWindowSeconds())
        );
        if (accessCount > properties.getMaxRequests()) {
            throw new ClientException("请求过于频繁，请稍后再试");
        }
        return true;
    }
}
