package com.thushima.statusmonitor.user.infraestructure.web;

import com.thushima.statusmonitor.user.application.UserService;
import com.thushima.statusmonitor.user.infraestructure.web.dto.RegisterUserRequest;
import com.thushima.statusmonitor.user.presentation.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public Mono<ResponseEntity<UserResponse>> register(@RequestBody @Valid RegisterUserRequest request) {
        return userService.register(request)
                .map(savedUser -> {
                    UserResponse response = UserResponse.fromDomain(savedUser);
                    return ResponseEntity
                            .created(URI.create("/users/" + savedUser.getId().value()))
                            .body(response);
                })
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity
                                .badRequest()
                                .build()));
    }

}
