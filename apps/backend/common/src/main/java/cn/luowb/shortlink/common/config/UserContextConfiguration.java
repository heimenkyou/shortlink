package cn.luowb.shortlink.common.config;

import cn.luowb.shortlink.common.biz.user.UserContextInterceptor;
import cn.luowb.shortlink.common.biz.user.UserInfoQueryService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户上下文拦截器配置。
 */
@AutoConfiguration
@ConditionalOnBean(UserInfoQueryService.class)
public class UserContextConfiguration {

    /**
     * 创建用户上下文拦截器。
     *
     * @param userInfoQueryService 用户查询服务
     * @param stringRedisTemplate Redis 客户端
     * @return 用户上下文拦截器
     */
    @Bean
    public UserContextInterceptor userContextInterceptor(
            UserInfoQueryService userInfoQueryService,
            StringRedisTemplate stringRedisTemplate
    ) {
        return new UserContextInterceptor(userInfoQueryService, stringRedisTemplate);
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
