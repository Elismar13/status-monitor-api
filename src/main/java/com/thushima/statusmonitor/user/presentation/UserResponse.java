package com.thushima.statusmonitor.user.presentation;

import com.thushima.statusmonitor.user.domain.User;

public record UserResponse(Long id, String email, String name, boolean active) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.getId().value(), user.getEmail().value(), user.getName(), user.isActive());
    }
}