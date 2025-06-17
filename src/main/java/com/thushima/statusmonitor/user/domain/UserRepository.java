package com.thushima.statusmonitor.user.domain;

import com.thushima.statusmonitor.user.infraestructure.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    List<User> findAll(Specification<UserEntity> spec);
}