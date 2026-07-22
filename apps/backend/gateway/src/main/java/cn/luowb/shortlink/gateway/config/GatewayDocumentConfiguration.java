package cn.luowb.shortlink.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 开发环境文档路由配置
 */
@Profile("dev")
@Configuration
public class GatewayDocumentConfiguration {

    /**
     * 将聚合文档请求转发到下游标准 OpenAPI 地址。
     *
     * @param builder 路由构建器
     * @return 开发环境文档路由
     */
    @Bean
    public RouteLocator documentRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("shortlink-admin-docs", route -> route
                        .order(-1)
                        .path("/api/short-link/admin/v3/api-docs")
                        .filters(filter -> filter.setPath("/v3/api-docs"))
                        .uri("lb://shortlink-admin"))
                .route("shortlink-project-docs", route -> route
                        .order(-1)
                        .path("/api/short-link/v3/api-docs")
                        .filters(filter -> filter.setPath("/v3/api-docs"))
                        .uri("lb://shortlink-project"))
                .build();
    }
}
