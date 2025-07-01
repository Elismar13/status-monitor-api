package com.thushima.statusmonitor.user.infraestructure;

import com.thushima.statusmonitor.user.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("roles")
public class RoleEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public static RoleEntity fromDomain(Role role) {
        return RoleEntity.builder()
                .name(role.value())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Role toDomain() {
        return new Role(this.name);
    }
}
