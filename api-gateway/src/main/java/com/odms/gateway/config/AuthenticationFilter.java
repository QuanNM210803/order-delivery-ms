package com.odms.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odms.gateway.dto.response.Response;
import com.odms.gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    private final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs", "/swagger-ui/", "/swagger-ui.html", "/swagger-ui/index.html",
            "/auth/auth/verify-token",
            "/auth/auth/login",
            "/auth/auth/register/customer",
    };

    @Value("${app.api-prefix}")
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean isPublicEndpoint = Arrays.stream(PUBLIC_ENDPOINTS).anyMatch(
            endpoint -> exchange.getRequest().getURI().getPath().contains(endpoint)
        );
        if (isPublicEndpoint){
            return chain.filter(exchange);
        }

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty()) {
            return unauthenticated(exchange.getResponse());
        }

        String token = authHeader.get(0).replace("Bearer ", "");

        return authService.verifyToken(token).flatMap(verifyResponse -> {
            if (verifyResponse.getData().getIsValid()){
                return chain.filter(exchange); // dung map thi tra ve Mono<Mono<Void>>
            } else {
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(
                throwable -> unauthenticated(exchange.getResponse())
        );
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        Response<?> apiResponse = Response.builder()
                .code(401)
                .message("Lỗi xác thực")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }
}
