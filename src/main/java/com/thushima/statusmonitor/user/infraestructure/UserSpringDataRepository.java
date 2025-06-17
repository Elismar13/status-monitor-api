package com.thushima.statusmonitor.user.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserSpringDataRepository extends
        JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}