package com.thushima.statusmonitor.user.infraestructure;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.Password;
import com.thushima.statusmonitor.user.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

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

    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.id = user.getId() != null ? user.getId().value() : null;
        entity.email = user.getEmail().value();
        entity.password = user.getPassword().value();
        entity.name = user.getName();
        entity.createdAt = user.getCreatedAt();
        entity.active = user.isActive();
        return entity;
    }

    public User toDomain() {
        return User.builder()
                .id(new User.UserId(id))
                .email(new Email(email))
                .password(new Password(password))
                .name(name)
                .createdAt(createdAt)
                .active(active)
                .build();
    }
}