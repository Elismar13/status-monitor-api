package com.thushima.statusmonitor.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class User {
    private final UserId id;
    private final Email email;
    private final Password password;
    private final String name;
    private final LocalDateTime createdAt;
    private boolean active;

    public record UserId(Long value) {
    }
}