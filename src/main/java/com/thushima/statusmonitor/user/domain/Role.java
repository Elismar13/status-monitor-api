package com.thushima.statusmonitor.user.domain;

import java.util.Objects;

public record Role(String value) {
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public Role {
        Objects.requireNonNull(value, "Role value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Role value cannot be blank");
        }
        if (!value.startsWith("ROLE_")) {
            throw new IllegalArgumentException("Role must start with 'ROLE_'");
        }
    }

    public static Role user() {
        return new Role(ROLE_USER);
    }

    public static Role admin() {
        return new Role(ROLE_ADMIN);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(value);
    }

    public boolean isUser() {
        return ROLE_USER.equals(value);
    }
}
