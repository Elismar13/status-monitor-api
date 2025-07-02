package com.thushima.statusmonitor.auth.infrastructure.web;

import com.thushima.statusmonitor.auth.application.AuthService;
import com.thushima.statusmonitor.auth.presentation.dto.AuthResponse;
import com.thushima.statusmonitor.auth.presentation.dto.LoginRequest;
import com.thushima.statusmonitor.auth.presentation.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password())
                .map(response -> ResponseEntity.ok(response));
    }

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request.refreshToken())
                .map(response -> ResponseEntity.ok(response));
    }
}
