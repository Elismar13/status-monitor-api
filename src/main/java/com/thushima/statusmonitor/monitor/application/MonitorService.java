package com.thushima.statusmonitor.monitor.application;

import com.thushima.statusmonitor.monitor.presentation.dto.MonitorResult;
import com.thushima.statusmonitor.project.domain.Project;
import com.thushima.statusmonitor.project.presentation.dto.ProjectResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MonitorService {

    Mono<MonitorResult> checkProjectStatus(Project project);

    Flux<MonitorResult> checkAllActiveProjects();

    Mono<ProjectResponse> getProjectStatus(UUID projectId);
}
