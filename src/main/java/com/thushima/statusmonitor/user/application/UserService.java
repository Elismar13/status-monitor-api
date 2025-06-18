package com.thushima.statusmonitor.user.application;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.Password;
import com.thushima.statusmonitor.user.domain.User;
import com.thushima.statusmonitor.user.domain.UserRepository;
import com.thushima.statusmonitor.user.infraestructure.web.dto.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Mono<User> register(RegisterUserRequest request) {
        Email email = new Email(request.email());
        if (Boolean.TRUE.equals(userRepo.existsByEmail(email).block())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        User user = User.builder()
                .id(null)
                .email(email)
                .password(new Password(request.password()))
                .name(request.name())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return userRepo.save(user);
    }

}