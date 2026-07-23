package cn.luowb.shortlink.common.config;

import cn.luowb.shortlink.common.biz.user.UserContextInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户上下文拦截器配置。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class UserContextConfiguration {

    /**
     * 创建用户上下文拦截器。
     *
     * @return 用户上下文拦截器
     */
    @Bean
    public UserContextInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }

    /**
     * 注册用户上下文拦截器。
     *
     * @param userContextInterceptor 用户上下文拦截器
     * @return MVC 配置器
     */
    @Bean
    public WebMvcConfigurer userContextWebMvcConfigurer(UserContextInterceptor userContextInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(userContextInterceptor).addPathPatterns("/api/**");
            }
        };
    }
}
