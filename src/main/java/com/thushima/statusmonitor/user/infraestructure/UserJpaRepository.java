package com.thushima.statusmonitor.user.infraestructure;

import com.thushima.statusmonitor.user.domain.Email;
import com.thushima.statusmonitor.user.domain.User;
import com.thushima.statusmonitor.user.domain.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserJpaRepository implements UserRepository {
    private final UserSpringDataRepository springDataRepo;

    public UserJpaRepository(UserSpringDataRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public List<User> findAll(Specification<UserEntity> spec) {
        return springDataRepo.findAll(spec).stream()
                .map(UserEntity::toDomain)
                .toList();
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        return springDataRepo.save(entity).toDomain();
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataRepo.findByEmail(email.value())
                .map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataRepo.existsByEmail(email.value());
    }

}