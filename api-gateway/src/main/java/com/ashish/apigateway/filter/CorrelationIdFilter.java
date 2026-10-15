package com.ashish.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    private static final String CorrelationId = "X-Correlation-Id";

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String currentCorrelationId = exchange.getResponse().getHeaders().getFirst(CorrelationId);
        if(currentCorrelationId == null){
            currentCorrelationId = UUID.randomUUID().toString();
            exchange.getResponse().getHeaders().add(CorrelationId,currentCorrelationId);
        }
        return chain.filter(exchange);
    }
}
