package com.thushima.statusmonitor.auth.application;

import com.thushima.statusmonitor.auth.presentation.dto.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResponse> login(String email, String password);

    Mono<AuthResponse> refreshToken(String refreshToken);
}
