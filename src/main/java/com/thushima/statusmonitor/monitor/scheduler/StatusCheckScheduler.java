package com.thushima.statusmonitor.monitor.scheduler;

import com.thushima.statusmonitor.monitor.application.impl.MonitorServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusCheckScheduler {

    private final MonitorServiceImpl monitorService;
    private Instant lastRun = Instant.now().minusSeconds(60);
    private boolean isRunning = false;

    @Scheduled(fixedDelayString = "${app.monitoring.check-interval:30000}")
    public void scheduleStatusChecks() {
        if (isRunning) {
            log.debug("Previous check is still running, skipping this cycle");
            return;
        }

        isRunning = true;
        log.debug("Starting scheduled status check at {}", Instant.now());

        monitorService.checkAllActiveProjects()
                .doOnError(e -> log.error("Error in scheduled status check: {}", e.getMessage(), e))
                .doFinally(signalType -> {
                    isRunning = false;
                    Duration duration = Duration.between(lastRun, Instant.now());
                    log.debug("Completed scheduled status check in {} ms", duration.toMillis());
                    lastRun = Instant.now();
                })
                .subscribe(
                        project -> log.trace("Checked project with current status {}", project.status()),
                        error -> log.error("Error in scheduled check: {}", error.getMessage())
                );
    }
}
