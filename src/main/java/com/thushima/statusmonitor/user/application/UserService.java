package com.thushima.statusmonitor.user.application;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.Password;
import com.thushima.statusmonitor.user.domain.User;
import com.thushima.statusmonitor.user.infraestructure.UserEntity;
import com.thushima.statusmonitor.user.infraestructure.repository.UserRepository;
import com.thushima.statusmonitor.user.infraestructure.web.dto.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<User> register(RegisterUserRequest request) {
        return Mono.defer(() -> {
            Email email = new Email(request.email());
            Password password = new Password(request.password());

            return userRepo.existsByEmail(email.value())
                    .flatMap(emailExists -> {
                        if (emailExists) {
                            return Mono.error(new IllegalArgumentException("Email already exists"));
                        }

                        String hashedPassword = passwordEncoder.encode(password.value());
                        User user = User.builder()
                                .email(email)
                                .password(new Password(hashedPassword, true))
                                .name(request.name())
                                .createdAt(LocalDateTime.now())
                                .active(true)
                                .build();

                        return userRepo.save(UserEntity.fromDomain(user))
                                .map(UserEntity::toDomain);
                    });
        });
    }
}