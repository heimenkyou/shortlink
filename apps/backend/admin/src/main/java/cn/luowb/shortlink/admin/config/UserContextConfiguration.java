package cn.luowb.shortlink.admin.config;

import cn.luowb.shortlink.admin.common.biz.user.UserContextInterceptor;
import cn.luowb.shortlink.admin.common.biz.user.UserFlowRiskControlInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户上下文拦截器配置
 */
@Configuration
@RequiredArgsConstructor
public class UserContextConfiguration implements WebMvcConfigurer {
    private final UserFlowRiskControlInterceptor userFlowRiskControlInterceptor;
    private final UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(userFlowRiskControlInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/short-link/admin/v1/user/login",
                        "/api/short-link/admin/v1/user/register",
                        "/api/short-link/admin/v1/user/has-username",
                        "/api/short-link/admin/v1/user/check-login"
                );
    }
}
