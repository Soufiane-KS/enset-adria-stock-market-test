package com.example.gatewayservice.filters;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (isWhitelisted(request.getPath().value())) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Missing Authorization header");
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(generateKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Authenticated-User", claims.getSubject())
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception ex) {
                return this.onError(exchange, "Invalid JWT: " + ex.getMessage());
            }
        };
    }

    private boolean isWhitelisted(String path) {
        return path.startsWith("/actuator") || path.startsWith("/api/auth");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("X-Error", error);
        return exchange.getResponse().setComplete();
    }

    private Key generateKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static class Config {
        // future configuration options
    }
}

