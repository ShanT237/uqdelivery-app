package com.uqdelivery.backend.infrastructure.persistence.adapter;

import com.uqdelivery.backend.domain.model.Role;
import com.uqdelivery.backend.domain.model.User;
import com.uqdelivery.backend.domain.port.out.UserRepositoryPort;
import com.uqdelivery.backend.infrastructure.persistence.entity.RoleEntity;
import com.uqdelivery.backend.infrastructure.persistence.entity.UserEntity;
import com.uqdelivery.backend.infrastructure.persistence.repository.RoleJpaRepository;
import com.uqdelivery.backend.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador que implementa el puerto de salida UserRepositoryPort.
 * Traduce entre la entidad de dominio (User) y la entidad JPA (UserEntity).
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                 RoleJpaRepository roleJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
    }

    // ─── Puerto implementado ─────────────────────────────────────────────────

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    // ─── Mapeos ──────────────────────────────────────────────────────────────

    private UserEntity toEntity(User user) {
        RoleEntity roleEntity = roleJpaRepository
                .findByName(user.getRole().name())
                .orElseGet(() -> roleJpaRepository.save(new RoleEntity(user.getRole().name())));

        // El id no se asigna: UserEntity usa @GeneratedValue y Hibernate lo genera al guardar.
        // Si se asigna a mano, save() hace merge, no encuentra la fila y falla con 500.
        UserEntity entity = new UserEntity();
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setRole(roleEntity);
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPassword(),
                Role.valueOf(entity.getRole().getName()),
                entity.getCreatedAt()
        );
    }
}
