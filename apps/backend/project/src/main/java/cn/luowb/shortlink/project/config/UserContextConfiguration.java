package cn.luowb.shortlink.project.config;

import cn.luowb.shortlink.project.common.biz.user.UserContextInterceptor;
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

    private final UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor).addPathPatterns("/api/**");
    }
}
