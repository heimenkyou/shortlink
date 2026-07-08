package cn.luowb.shortlink.admin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
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
public class SaTokenConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/short-link/v1/user/login",
                        "/api/short-link/v1/user/has-username",
                        "/api/short-link/v1/user/check-login"
//                        "/api/short-link/v1/user"
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