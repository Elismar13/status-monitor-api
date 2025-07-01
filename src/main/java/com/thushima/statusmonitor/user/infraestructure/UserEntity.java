package com.thushima.statusmonitor.user.infraestructure;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.Password;
import com.thushima.statusmonitor.user.domain.Role;
import com.thushima.statusmonitor.user.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    private Long id;
    private String email;
    private String password;
    private String name;
    private LocalDateTime createdAt;
    private boolean active;

    @Transient
    private Set<RoleEntity> roles = new HashSet<>();

    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.id = user.getId() != null ? user.getId().value() : null;
        entity.email = user.getEmail().value();
        entity.password = user.getPassword().value();
        entity.name = user.getName();
        entity.createdAt = user.getCreatedAt();
        entity.active = user.isActive();

        if (user.getRoles() != null) {
            entity.roles = user.getRoles().stream()
                    .map(RoleEntity::fromDomain)
                    .collect(Collectors.toSet());
        }
        
        return entity;
    }

    public User toDomain() {
        Set<Role> domainRoles = this.roles.stream()
                .map(RoleEntity::toDomain)
                .collect(Collectors.toSet());
                
        return User.builder()
                .id(new User.UserId(id))
                .email(new Email(email))
                .password(new Password(password))
                .name(name)
                .createdAt(createdAt)
                .active(active)
                .roles(domainRoles)
                .build();
    }
}