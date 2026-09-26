package com.agilesprint.config;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();
        long start = System.nanoTime();

        exchange.getResponse().getHeaders().add("X-Request-Id", requestId);
        log.info("Request started: id={}, method={}, path={}",
                requestId, request.getMethod(), request.getURI().getPath());

        return chain.filter(exchange)
                .doOnSuccess(ignored -> log.info("Request completed: id={}, method={}, path={}, status={}, durationMs={}",
                        requestId, request.getMethod(), request.getURI().getPath(),
                        exchange.getResponse().getStatusCode(), elapsedMillis(start)))
                .doOnError(error -> {
                    if (error instanceof NoResourceFoundException) {
                        log.warn("Resource not found: id={}, method={}, path={}, status=404, durationMs={}",
                                requestId, request.getMethod(), request.getURI().getPath(),
                                elapsedMillis(start));
                    } else {
                        log.error("Request failed: id={}, method={}, path={}, status={}, durationMs={}",
                                requestId, request.getMethod(), request.getURI().getPath(),
                                exchange.getResponse().getStatusCode(), elapsedMillis(start), error);
                    }
                });
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
