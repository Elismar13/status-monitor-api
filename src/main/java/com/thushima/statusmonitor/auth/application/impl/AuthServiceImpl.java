package com.thushima.statusmonitor.auth.application.impl;

import com.thushima.statusmonitor.auth.application.AuthService;
import com.thushima.statusmonitor.auth.presentation.dto.AuthResponse;
import com.thushima.statusmonitor.security.jwt.JwtUtil;
import com.thushima.statusmonitor.shared.exception.AuthenticationException;
import com.thushima.statusmonitor.shared.exception.ResourceNotFoundException;
import com.thushima.statusmonitor.user.application.UserService;
import com.thushima.statusmonitor.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<AuthResponse> login(String email, String password) {
        return userService.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "email", email)))
                .filter(user -> validatePassword(email, password, user))
                .switchIfEmpty(Mono.error(new AuthenticationException("Invalid credentials")))
                .flatMap(this::generateTokens)
                .doOnSuccess(response -> log.info("User {} logged in successfully", email))
                .doOnError(error -> log.error("Login failed for user: {}", email, error));
    }

    @Override
    public Mono<AuthResponse> refreshToken(String refreshToken) {
        return Mono.just(refreshToken)
                .filterWhen(jwtUtil::isTokenInvalidOrExpired)
                .switchIfEmpty(Mono.error(new AuthenticationException("Invalid or expired refresh token")))
                .flatMap(jwtUtil::extractEmail)
                .flatMap(email -> userService.findByEmail(email)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "email", email))))
                .flatMap(this::generateTokens)
                .doOnSuccess(response -> log.info("Tokens refreshed successfully"))
                .doOnError(error -> log.error("Token refresh failed", error));
    }

    private boolean validatePassword(String email, String rawPassword, User user) {
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword().value());
        if (!matches) {
            log.warn("Invalid password attempt for user: {}", email);
        }
        return matches;
    }

    private Mono<AuthResponse> generateTokens(User user) {
        try {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            long expiresIn = jwtUtil.getExpirationTime() / 1000;
            return Mono.just(AuthResponse.of(accessToken, refreshToken, expiresIn));
        } catch (Exception e) {
            log.error("Error generating tokens: {}", e.getMessage(), e);
            return Mono.error(new AuthenticationException("Error generating authentication tokens"));
        }
    }
}
