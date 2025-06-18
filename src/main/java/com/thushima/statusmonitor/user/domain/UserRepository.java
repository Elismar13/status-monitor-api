package com.thushima.statusmonitor.user.domain;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {
    Mono<User> save(User user);

    Mono<User> findByEmail(Email email);

    Mono<Boolean> existsByEmail(Email email);

    Flux<User> findAll();
}