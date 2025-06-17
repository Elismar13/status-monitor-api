package com.thushima.statusmonitor.user.infraestructure;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.Password;
import com.thushima.statusmonitor.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
                .password(new Password(password, true))
                .name(name)
                .createdAt(createdAt)
                .active(active)
                .build();
    }
}