package com.thushima.statusmonitor.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class User {
    private final UserId id;
    private final Email email;
    private final Password password;
    private final String name;
    private final LocalDateTime createdAt;
    private final Set<Role> roles;
    private boolean active;

    public record UserId(Long value) {
        public UserId {
            if (value != null && value <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
        }
    }
}