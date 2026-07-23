package cn.luowb.shortlink.admin.config;

import cn.luowb.shortlink.admin.common.biz.user.UserFlowRiskControlInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 管理端用户频控拦截器配置。
 */
@Configuration
@RequiredArgsConstructor
public class UserFlowRiskControlConfiguration implements WebMvcConfigurer {

    private final UserFlowRiskControlInterceptor userFlowRiskControlInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
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
