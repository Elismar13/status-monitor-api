package com.thushima.statusmonitor.project.infrastructure.web;

import com.thushima.statusmonitor.project.application.ProjectService;
import com.thushima.statusmonitor.project.presentation.dto.ProjectRequest;
import com.thushima.statusmonitor.project.presentation.dto.ProjectResponse;
import com.thushima.statusmonitor.user.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProjectResponse> create(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal String email) {
        return projectService.create(request, email);
    }

    @GetMapping("/{id}")
    public Mono<ProjectResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal String email) {
        return projectService.getById(id, email);
    }

    @GetMapping
    public Flux<ProjectResponse> getAll(@AuthenticationPrincipal String email) {
        return projectService.getAllByUserEmail(email);
    }

    @GetMapping("/dashboard/summary")
    public Flux<ProjectResponse> getSummary(@AuthenticationPrincipal String email) {
        return projectService.getAllByUserEmail(email);
    }

    @PutMapping("/{id}")
    public Mono<ProjectResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal String email) {
        return projectService.update(id, request, email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal String email) {
        return projectService.delete(id, email);
    }
}
