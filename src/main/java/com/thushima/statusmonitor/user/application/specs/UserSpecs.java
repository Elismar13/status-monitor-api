package com.thushima.statusmonitor.user.application.specs;

import com.thushima.statusmonitor.user.infraestructure.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {
    public static Specification<UserEntity> isActive() {
        return (root, query, builder) -> builder.isTrue(root.get("active"));
    }

    public static Specification<UserEntity> hasEmailLike(String domain) {
        return (root, query, builder) -> builder.like(root.get("email"), "%@" + domain);
    }
}