package com.thushima.statusmonitor.monitor.presentation.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record MonitorResult(
        String status,
        int statusCode,
        String errorMessage,
        long responseTimeMs,
        Instant checkedAt
) {
    public static MonitorResult up(int statusCode, long responseTimeMs) {
        return new MonitorResult("UP", statusCode, null, responseTimeMs, Instant.now());
    }

    public static MonitorResult down(int statusCode, String errorMessage) {
        return new MonitorResult("DOWN", statusCode, errorMessage, 0, Instant.now());
    }

    public boolean isUp() {
        return "UP".equals(status);
    }
}
