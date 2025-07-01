package com.thushima.statusmonitor.user.domain;

import com.thushima.statusmonitor.user.infraestructure.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Integer> {
    @Override
    Mono<UserEntity> save(UserEntity user);

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    @Override
    Flux<UserEntity> findAll();

}