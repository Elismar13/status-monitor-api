package com.thushima.statusmonitor.user.infraestructure.web.dto;

import com.thushima.statusmonitor.user.application.specs.UserSpecs;
import com.thushima.statusmonitor.user.infraestructure.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public record UserSearchRequest(String domain, boolean onlyActive) {
    public Specification<UserEntity> toSpec() {
        Specification<UserEntity> spec = Specification.where(null);
        if (onlyActive) spec = spec.and(UserSpecs.isActive());
        if (domain != null) spec = spec.and(UserSpecs.hasEmailLike(domain));
        return spec;
    }
}