package com.thushima.statusmonitor.monitor.application.impl;

import com.thushima.statusmonitor.monitor.application.MonitorService;
import com.thushima.statusmonitor.monitor.infrastructure.webclient.StatusCheckerWebClient;
import com.thushima.statusmonitor.monitor.presentation.dto.MonitorResult;
import com.thushima.statusmonitor.project.domain.Project;
import com.thushima.statusmonitor.project.domain.ProjectStatus;
import com.thushima.statusmonitor.project.infrastructure.repository.ProjectRepository;
import com.thushima.statusmonitor.project.presentation.dto.ProjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final ProjectRepository projectRepository;
    private final StatusCheckerWebClient statusChecker;

    @Transactional
    public Mono<MonitorResult> checkProjectStatus(Project project) {
        return statusChecker.checkStatus(project.getUrl(), project.getTimeoutInSeconds())
                .flatMap(result -> {
                    log.debug("Status check for project {} ({}): {}",
                            project.getName(), project.getUrl(), result.status());
                    return updateProjectStatus(project, result);
                });
    }

    @Transactional
    public Flux<MonitorResult> checkAllActiveProjects() {
        return projectRepository.findByActiveTrue()
                .flatMap(project -> checkProjectStatus(project)
                        .onErrorResume(e -> {
                            log.error("Error checking status for project {}: {}",
                                    project.getId(), e.getMessage());
                            return Mono.empty();
                        })
                );
    }

    private Mono<MonitorResult> updateProjectStatus(Project project, MonitorResult result) {
        boolean isUp = result.isUp();
        LocalDateTime now = LocalDateTime.now();

        long uptimeIncrement = isUp ? project.getCheckIntervalInMinutes() * 60 : 0;
        long downtimeIncrement = isUp ? 0 : project.getCheckIntervalInMinutes() * 60;

        project.setLastStatus(isUp ? ProjectStatus.UP.getCode() : ProjectStatus.DOWN.getCode());
        project.setLastCheckedAt(now);

        project.setTotalUptimeInSeconds(
                (project.getTotalUptimeInSeconds() != null ? project.getTotalUptimeInSeconds() : 0) + uptimeIncrement
        );
        project.setTotalDowntimeInSeconds(
                (project.getTotalDowntimeInSeconds() != null ? project.getTotalDowntimeInSeconds() : 0) + downtimeIncrement
        );

        long total = project.getTotalUptimeInSeconds() + project.getTotalDowntimeInSeconds();
        double uptimePercentage = total > 0 ?
                (project.getTotalUptimeInSeconds() * 100.0) / total : 100.0;
        project.setUptimePercentage(uptimePercentage);

        return projectRepository.save(project)
                .thenReturn(result);
    }

    @Transactional
    public Mono<ProjectResponse> getProjectStatus(UUID projectId) {
        return projectRepository.findById(projectId)
                .flatMap(project -> statusChecker.checkStatus(project.getUrl(), project.getTimeoutInSeconds())
                        .flatMap(result -> updateProjectStatus(project, result)
                                .thenReturn(ProjectResponse.fromDomain(project)))
                );
    }
}
