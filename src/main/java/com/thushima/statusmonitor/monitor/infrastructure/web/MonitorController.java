package com.thushima.statusmonitor.monitor.infrastructure.web;

import com.thushima.statusmonitor.monitor.application.MonitorService;
import com.thushima.statusmonitor.monitor.presentation.dto.MonitorResult;
import com.thushima.statusmonitor.project.presentation.dto.ProjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monitor")
public class MonitorController {

    private final MonitorService monitorService;

    @Autowired
    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @PatchMapping("/check/{projectId}")
//    @PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<MonitorResult>> checkProjectStatus(
            @PathVariable UUID projectId,
            @RequestParam(required = false, defaultValue = "30") int timeoutInSeconds) {

        return monitorService.getProjectStatus(projectId)
                .map(project -> ResponseEntity.ok(MonitorResult.builder()
                        .status(project.lastStatus())
                        .checkedAt(Instant.now())
                        .statusCode(200)
                        .build()
                ))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{projectId}")
//    @PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<ProjectResponse>> getProjectStatus(@PathVariable UUID projectId) {
        return monitorService.getProjectStatus(projectId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
