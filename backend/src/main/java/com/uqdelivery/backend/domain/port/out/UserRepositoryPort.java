package com.uqdelivery.backend.domain.port.out;

import com.uqdelivery.backend.domain.model.User;

import java.util.Optional;

/**
 * Puerto de salida (repositorio) para la entidad User.
 * La implementación concreta vive en la capa de infraestructura.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
