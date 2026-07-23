package cn.luowb.shortlink.gateway.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.reactor.spring.SaTokenContextRegister;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;

import static cn.luowb.shortlink.gateway.filter.UserInfoTransmitFilter.REAL_NAME_HEADER;
import static cn.luowb.shortlink.gateway.filter.UserInfoTransmitFilter.USERNAME_HEADER;
import static cn.luowb.shortlink.gateway.filter.UserInfoTransmitFilter.USER_ID_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sa-Token 响应式上下文测试
 */
class SaReactorFilterContextTest {

    @Test
    void shouldInitializeContextBeforeAuthentication() {
        new SaTokenContextRegister();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"));
        AtomicBoolean contextInitialized = new AtomicBoolean();
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(ignored -> contextInitialized.set(SaHolder.getStorage() != null));

        Mono<Void> result = filter.filter(exchange, ignored -> Mono.empty());

        StepVerifier.create(result).verifyComplete();
        org.junit.jupiter.api.Assertions.assertTrue(contextInitialized.get());
    }

    @Test
    void shouldTransmitTrustedUserInfoHeaders() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test")
                .header(USER_ID_HEADER, "999")
                .header(USERNAME_HEADER, "forged"));
        exchange.getAttributes().put(UserInfoTransmitFilter.USER_ATTRIBUTE, Map.of(
                "id", 1L,
                "username", "luowb",
                "realName", "罗文彬"
        ));

        Mono<Void> result = new UserInfoTransmitFilter().filter(exchange, forwardedExchange -> {
            assertEquals("1", forwardedExchange.getRequest().getHeaders().getFirst(USER_ID_HEADER));
            assertEquals("luowb", forwardedExchange.getRequest().getHeaders().getFirst(USERNAME_HEADER));
            assertEquals(
                    "%E7%BD%97%E6%96%87%E5%BD%AC",
                    forwardedExchange.getRequest().getHeaders().getFirst(REAL_NAME_HEADER)
            );
            return Mono.empty();
        });

        StepVerifier.create(result).verifyComplete();
    }
}
