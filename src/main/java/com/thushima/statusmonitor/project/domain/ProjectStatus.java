package com.thushima.statusmonitor.project.domain;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    UP((short) 1, "The project is up and running"),
    DOWN((short) 0, "The project is down"),
    UNKNOWN((short) -1, "The status is unknown");

    private final short code;
    private final String description;

    ProjectStatus(short code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ProjectStatus fromCode(short code) {
        for (ProjectStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNKNOWN;
    }

    public static ProjectStatus fromString(String status) {
        if (status == null) {
            return UNKNOWN;
        }
        try {
            return ProjectStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
