package com.thushima.statusmonitor.project.presentation.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DashboardSummaryResponse(
        long totalProjects,
        long upCount,
        long downCount,
        Instant lastCheckedAt
) {
    public static DashboardSummaryResponse of(int totalProjects, int upCount, int downCount, Instant lastCheckedAt) {
        return new DashboardSummaryResponse(totalProjects, upCount, downCount, lastCheckedAt);
    }
}
