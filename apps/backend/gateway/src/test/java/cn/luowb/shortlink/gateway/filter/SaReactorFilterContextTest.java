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
}
