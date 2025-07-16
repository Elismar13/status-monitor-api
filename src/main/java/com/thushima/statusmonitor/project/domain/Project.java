package com.thushima.statusmonitor.project.domain;

import com.thushima.statusmonitor.project.presentation.dto.ProjectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("projects")
public class Project {
    
    @Id
    private UUID id;
    
    private String name;
    private String description;
    private String url;
    private boolean active;
    private Long userId;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Status monitoring fields
    private Integer checkIntervalInMinutes;
    private Integer timeoutInSeconds;
    private Integer successThreshold;
    private Integer failureThreshold;
    
    // Status fields (cached)
    private String lastStatus;
    private LocalDateTime lastCheckedAt;
    private Double uptimePercentage;
    private Long totalUptimeInSeconds;
    private Long totalDowntimeInSeconds;

    public static Project buildNewProject(ProjectRequest request, Long userId) {
        return Project.builder()
                .name(request.name())
                .description(request.description())
                .url(request.url())
                .active(request.active())
                .userId(userId)
                .checkIntervalInMinutes(request.checkIntervalInMinutes())
                .timeoutInSeconds(request.timeoutInSeconds())
                .successThreshold(request.successThreshold())
                .failureThreshold(request.failureThreshold())
                .uptimePercentage(100.0) // Start with 100% uptime
                .totalUptimeInSeconds(0L)
                .totalDowntimeInSeconds(0L)
                .lastCheckedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
