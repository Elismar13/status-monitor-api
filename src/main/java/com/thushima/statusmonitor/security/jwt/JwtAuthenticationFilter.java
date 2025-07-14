package com.thushima.statusmonitor.security.jwt;

import com.thushima.statusmonitor.constants.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = resolveToken(exchange.getRequest().getHeaders());

        if (!StringUtils.hasText(token)) {
            log.debug("No JWT token found in request headers");
            return chain.filter(exchange);
        }

        log.debug("Processing JWT token authentication");

        return jwtUtil.validateToken(token)
                .doOnSuccess(claims -> log.debug("JWT token is valid"))
                .doOnError(e -> log.debug("JWT token is invalid: {}", e.getMessage()))
                .flatMap(claims -> jwtUtil.getAuthentication(token))
                .doOnSuccess(auth -> log.debug("Successfully created authentication for principal: {}", auth.getPrincipal()))
                .flatMap(authentication -> {
                    log.debug("Setting authentication in security context");
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                })
                .onErrorResume(e -> {
                    log.debug("Failed to set authentication in security context: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private String resolveToken(HttpHeaders headers) {
        String bearerToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(SecurityConstants.BEARER_PREFIX)) {
            log.trace("JWT token found in request headers");
            return bearerToken.substring(7);
        }
        log.trace("JWT token not found in request headers");
        return null;
    }
}