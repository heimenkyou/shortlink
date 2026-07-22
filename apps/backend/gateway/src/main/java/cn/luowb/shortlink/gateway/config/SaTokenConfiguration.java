package cn.luowb.shortlink.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpUtil;
import cn.luowb.shortlink.gateway.filter.TokenValidateFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关 Sa-Token 配置
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfiguration {

    private static final String UNAUTHORIZED_BODY = "{\"code\":\"A000001\",\"message\":\"未登录\"}";

    private final Config config;

    /**
     * 使用 Sa-Token 官方响应式过滤器完成认证和活跃续期。
     *
     * @return Sa-Token 响应式过滤器
     */
    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .setExcludeList(config.getWhitePathList())
                .setAuth(ignored -> {
                    StpUtil.checkLogin();
                    SaReactorSyncHolder.getExchange().getAttributes().put(
                            TokenValidateFilter.USER_ATTRIBUTE, StpUtil.getLoginIdAsString());
                })
                .setError(error -> {
                    SaHolder.getResponse()
                            .setStatus(401)
                            .setHeader("Content-Type", "application/json;charset=UTF-8");
                    return UNAUTHORIZED_BODY;
                });
    }
}
