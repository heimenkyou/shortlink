package cn.luowb.shortlink.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 向下游透传网关认证后的用户信息。
 */
@Component
public class UserInfoTransmitFilter implements GlobalFilter, Ordered {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";
    public static final String REAL_NAME_HEADER = "X-Real-Name";
    public static final String USER_ATTRIBUTE = UserInfoTransmitFilter.class.getName() + ".userInfo";

    /**
     * 客户端传入的用户 Header 不可信，转发前必须由网关覆盖。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate().headers(headers -> {
            headers.remove(USER_ID_HEADER);
            headers.remove(USERNAME_HEADER);
            headers.remove(REAL_NAME_HEADER);
        });
        Map<?, ?> userInfo = exchange.getAttribute(USER_ATTRIBUTE);
        if (userInfo != null) {
            requestBuilder.header(USER_ID_HEADER, String.valueOf(userInfo.get("id")));
            requestBuilder.header(
                    USERNAME_HEADER,
                    UriUtils.encode(String.valueOf(userInfo.get("username")), StandardCharsets.UTF_8)
            );
            String realName = (String) userInfo.get("realName");
            if (StringUtils.hasText(realName)) {
                requestBuilder.header(REAL_NAME_HEADER, UriUtils.encode(realName, StandardCharsets.UTF_8));
            }
        }
        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
