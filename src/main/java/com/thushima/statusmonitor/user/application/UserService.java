package com.thushima.statusmonitor.user.application;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.Password;
import com.thushima.statusmonitor.user.domain.User;
import com.thushima.statusmonitor.user.domain.UserRepository;
import com.thushima.statusmonitor.user.infraestructure.UserEntity;
import com.thushima.statusmonitor.user.infraestructure.web.dto.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User register(RegisterUserRequest request) {
        Email email = new Email(request.email());
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já existe");
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

    public List<User> search(Specification<UserEntity> spec) {
        return userRepo.findAll(spec);
    }
}