package com.thushima.statusmonitor.project.application;

import com.thushima.statusmonitor.project.presentation.dto.DashboardSummaryResponse;
import com.thushima.statusmonitor.project.presentation.dto.ProjectRequest;
import com.thushima.statusmonitor.project.presentation.dto.ProjectResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProjectService {
    
    Mono<ProjectResponse> create(ProjectRequest request, String userEmail);
    
    Mono<ProjectResponse> getById(UUID id, String userEmail);
    
    Flux<ProjectResponse> getAllByUserEmail(String userEmail);

    Mono<DashboardSummaryResponse> getDashboardSummary(String userEmail);

    Mono<ProjectResponse> update(UUID id, ProjectRequest request, String userEmail);
    
    Mono<Void> delete(UUID id, String userEmail);
    
    Mono<Boolean> existsByIdAndUserEmail(UUID id, String userEmail);
}
