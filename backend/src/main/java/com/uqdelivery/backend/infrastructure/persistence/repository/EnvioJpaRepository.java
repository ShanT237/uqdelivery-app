package com.uqdelivery.backend.infrastructure.persistence.repository;

import com.uqdelivery.backend.infrastructure.persistence.entity.EnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para EnvioEntity.
 */
public interface EnvioJpaRepository extends JpaRepository<EnvioEntity, UUID> {

    Optional<EnvioEntity> findByCodigoSeguimiento(String codigoSeguimiento);
}
