package com.thushima.statusmonitor.project.application.impl;

import com.thushima.statusmonitor.project.application.ProjectService;
import com.thushima.statusmonitor.project.domain.Project;
import com.thushima.statusmonitor.project.domain.ProjectStatus;
import com.thushima.statusmonitor.project.infrastructure.repository.ProjectRepository;
import com.thushima.statusmonitor.project.presentation.dto.DashboardSummaryResponse;
import com.thushima.statusmonitor.project.presentation.dto.ProjectRequest;
import com.thushima.statusmonitor.project.presentation.dto.ProjectResponse;
import com.thushima.statusmonitor.shared.exception.ResourceNotFoundException;
import com.thushima.statusmonitor.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    public Mono<ProjectResponse> create(ProjectRequest request, String userEmail) {
        return userService.findByEmail(userEmail)
                .flatMap(user -> {
                    Project project = Project.buildNewProject(request, user.getId().value());
                    return projectRepository.save(project)
                            .map(ProjectResponse::fromDomain)
                            .doOnSuccess(p -> log.info("Created project: {} for user: {}", p.id(), userEmail))
                            .doOnError(error -> log.error("Error creating project: {}", error.getMessage()));
                });
    }

    @Override
    public Mono<ProjectResponse> getById(UUID id, String userEmail) {
        return validateUserAndProject(id, userEmail)
                .map(ProjectResponse::fromDomain);
    }

    @Override
    public Flux<ProjectResponse> getAllByUserEmail(String userEmail) {
        return validateUserAndRetrieveProjects(userEmail)
                .map(ProjectResponse::fromDomain);
    }

    @Override
    public Mono<DashboardSummaryResponse> getDashboardSummary(String userEmail) {
        return validateUserAndRetrieveProjects(userEmail)
                .collectList()
                .map(projects -> {
                    long totalProjects = projects.size();
                    long activeProjects = projects.stream()
                            .filter(Project::isActive).count();
                    long upCount = projects.stream()
                            .filter(Project::isActive)
                            .filter(project -> project.getLastStatus() == ProjectStatus.UP.getCode())
                            .count();
                    long downCount = activeProjects - upCount;

                    return DashboardSummaryResponse.builder()
                            .totalProjects(totalProjects)
                            .upCount(upCount)
                            .downCount(downCount)
                            .build();
                })
                .doOnSuccess(p -> log.info("Retrieved dashboard summary for user: {}", userEmail))
                .doOnError(error -> log.error("Error retrieving dashboard summary: {}", error.getMessage()));
    }

    @Override
    public Mono<ProjectResponse> update(UUID id, ProjectRequest request, String userEmail) {
        return validateUserAndProject(id, userEmail)
                .flatMap(existingProject -> {
                    existingProject.setName(request.name());
                    existingProject.setDescription(request.description());
                    existingProject.setUrl(request.url());
                    existingProject.setActive(request.active());
                    existingProject.setCheckIntervalInMinutes(request.checkIntervalInMinutes());
                    existingProject.setTimeoutInSeconds(request.timeoutInSeconds());
                    existingProject.setSuccessThreshold(request.successThreshold());
                    existingProject.setFailureThreshold(request.failureThreshold());
                    existingProject.setUpdatedAt(LocalDateTime.now());

                    return projectRepository.save(existingProject);
                })
                .map(ProjectResponse::fromDomain)
                .doOnSuccess(p -> log.info("Updated project: {} for user: {}", id, userEmail))
                .doOnError(error -> log.error("Error updating project: {}", error.getMessage()));
    }

    @Override
    public Mono<Void> delete(UUID id, String userEmail) {
        return userService.findByEmail(userEmail)
                .flatMap(user -> projectRepository.findByIdAndUserId(id, user.getId().value())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Project", "id", id.toString())))
                        .flatMap(project -> projectRepository.delete(project))
                        .doOnSuccess(v -> log.info("Deleted project: {} for user: {}", id, userEmail))
                        .doOnError(error -> log.error("Error deleting project: {}", error.getMessage())));
    }

    @Override
    public Mono<Boolean> existsByIdAndUserEmail(UUID id, String userEmail) {
        return userService.findByEmail(userEmail)
                .flatMap(user -> projectRepository.existsByIdAndUserId(id, user.getId().value()));
    }

    private Mono<Project> validateUserAndProject(UUID id, String userEmail) {
        return userService.findByEmail(userEmail)
                .flatMap(user -> projectRepository.findByIdAndUserId(id, user.getId().value()))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Project", "id", id.toString())));
    }

    private Flux<Project> validateUserAndRetrieveProjects(String userEmail) {
        return userService.findByEmail(userEmail)
                .flatMapMany(user -> projectRepository.findAllByUserId(user.getId().value()))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Project", "user", userEmail)));
    }
}
