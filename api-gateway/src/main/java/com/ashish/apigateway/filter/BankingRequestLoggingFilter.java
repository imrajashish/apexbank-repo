package com.ashish.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class BankingRequestLoggingFilter  implements GlobalFilter, Ordered {
    @Override
    public int getOrder(){
        return  -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String RequestId = UUID.randomUUID().toString();
        exchange.getResponse().getHeaders().add("X-Request-ID",RequestId);
        String Path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String user = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        log.info("[REQ] id={} user= {} {} {}",RequestId,user,method, Path);
        return chain.filter(exchange).doOnSuccess(v -> {
            long timeTaken = System.currentTimeMillis()- startTime;
            log.info("[RES] id={} status = {} time={}ms",RequestId,exchange.getResponse().getStatusCode(),timeTaken);
        }).doOnError(ex -> {
            long timeTaken = System.currentTimeMillis()- startTime;
            log.error("[err] id={} {} {} time={}ms",RequestId, method, Path,timeTaken, ex);
        });
    }

}
