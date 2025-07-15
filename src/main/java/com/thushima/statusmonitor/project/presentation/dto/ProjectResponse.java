package com.thushima.statusmonitor.project.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thushima.statusmonitor.project.domain.Project;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectResponse(
    String id,
    String name,
    String description,
    String url,
    boolean active,
    Integer checkIntervalInMinutes,
    Integer timeoutInSeconds,
    Integer successThreshold,
    Integer failureThreshold,
    String lastStatus,
    LocalDateTime lastCheckedAt,
    Double uptimePercentage,
    Long totalUptimeInSeconds,
    Long totalDowntimeInSeconds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProjectResponse fromDomain(Project project) {
        return new ProjectResponse(
            project.getId().toString(),
            project.getName(),
            project.getDescription(),
            project.getUrl(),
            project.isActive(),
            project.getCheckIntervalInMinutes(),
            project.getTimeoutInSeconds(),
            project.getSuccessThreshold(),
            project.getFailureThreshold(),
            project.getLastStatus(),
            project.getLastCheckedAt(),
            project.getUptimePercentage(),
            project.getTotalUptimeInSeconds(),
            project.getTotalDowntimeInSeconds(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}
