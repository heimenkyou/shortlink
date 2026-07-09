package cn.luowb.shortlink.admin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.luowb.shortlink.admin.common.biz.user.UserContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 配置类
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfiguration implements WebMvcConfigurer {
    private final UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Sa-Token 登录拦截器，检查用户是否登录
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/short-link/v1/user/login",
                        "/api/short-link/v1/user/has-username",
                        "/api/short-link/v1/user/check-login"
//                        "/api/short-link/v1/user"
                );
        // 用户信息上下文拦截器，装配用户信息
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/short-link/v1/user/login",
                        "/api/short-link/v1/user/has-username",
                        "/api/short-link/v1/user/check-login"
                );
    }


    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                return new ArrayList<>();
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                return new ArrayList<>();
            }
        };
    }
}