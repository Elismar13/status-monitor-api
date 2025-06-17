package com.thushima.statusmonitor.user.infraestructure.web;

import com.thushima.statusmonitor.user.application.UserService;
import com.thushima.statusmonitor.user.infraestructure.web.dto.RegisterUserRequest;
import com.thushima.statusmonitor.user.infraestructure.web.dto.UserSearchRequest;
import com.thushima.statusmonitor.user.presentation.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public UserResponse register(@RequestBody @Valid RegisterUserRequest request) {
        return UserResponse.fromDomain(userService.register(request));
    }

    @PostMapping("/search")
    public List<UserResponse> search(@RequestBody @Valid UserSearchRequest request) {
        return userService.search(request.toSpec())
                .stream()
                .map(UserResponse::fromDomain)
                .toList();
    }
}
