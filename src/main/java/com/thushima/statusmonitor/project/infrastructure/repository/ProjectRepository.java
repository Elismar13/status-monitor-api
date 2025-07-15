package com.thushima.statusmonitor.project.infrastructure.repository;

import com.thushima.statusmonitor.project.domain.Project;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProjectRepository extends R2dbcRepository<Project, UUID> {
    
    Flux<Project> findByUserId(Long userId);

    Flux<Project> findAllByUserId(Long userId);

    Mono<Project> findByIdAndUserId(UUID id, Long userId);
    
    Mono<Boolean> existsByIdAndUserId(UUID id, Long userId);
    
    Mono<Void> deleteByIdAndUserId(UUID id, Long userId);
}
