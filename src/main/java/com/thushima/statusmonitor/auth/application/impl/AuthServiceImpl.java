package com.thushima.statusmonitor.auth.application.impl;

import com.thushima.statusmonitor.auth.application.AuthService;
import com.thushima.statusmonitor.auth.domain.exception.InvalidRefreshTokenException;
import com.thushima.statusmonitor.auth.presentation.dto.AuthResponse;
import com.thushima.statusmonitor.security.jwt.JwtUtil;
import com.thushima.statusmonitor.user.application.UserService;
import com.thushima.statusmonitor.user.domain.User;
import com.thushima.statusmonitor.user.infraestructure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public Mono<AuthResponse> login(String username, String password) {
        return userService.findByEmail(username)
                .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException("User not found with email: " + username)))
                .filter(user -> validatePassword(username, password, user))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials")))
                .flatMap(this::generateTokens);
    }

    @Override
    public Mono<AuthResponse> refreshToken(String refreshToken) {
        return Mono.just(refreshToken)
                .filter(token -> Boolean.TRUE.equals(jwtUtil.isTokenInvalidOrExpired(token).block()))
                .flatMap(jwtUtil::extractEmail)
                .switchIfEmpty(Mono.error(new InvalidRefreshTokenException("Invalid refresh token")))
                .flatMap(email -> userService.findByEmail(email)
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email: " + email))))
                .flatMap(this::generateTokens);
    }

    private boolean validatePassword(String username, String rawPassword, User user) {
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword().value());
        if (!matches) {
            log.warn("Invalid password attempt for user: {}", username);
        }
        return matches;
    }

    private Mono<AuthResponse> generateTokens(User user) {
        try {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            long expiresIn = jwtUtil.getExpirationTime() / 1000;

            log.info("Generated tokens for user: {}", user.getEmail().value());
            return Mono.just(AuthResponse.of(accessToken, refreshToken, expiresIn));
        } catch (Exception e) {
            log.error("Error generating tokens for user: {}", user.getEmail().value(), e);
            return Mono.error(new AuthenticationServiceException("Error during token generation"));
        }
    }
}
