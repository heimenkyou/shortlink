package cn.luowb.shortlink.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 向下游传递认证后的用户名
 */
@Component
public class TokenValidateFilter implements GlobalFilter, Ordered {

    public static final String USER_HEADER = "X-Username";
    public static final String USER_ATTRIBUTE = TokenValidateFilter.class.getName() + ".username";

    /**
     * 客户端传入的用户 Header 不可信，转发前必须由网关覆盖。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate().headers(headers -> headers.remove(USER_HEADER));
        String username = exchange.getAttribute(USER_ATTRIBUTE);
        if (StringUtils.hasText(username)) {
            requestBuilder.header(USER_HEADER, username);
        }
        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
