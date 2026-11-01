package com.uqdelivery.backend.infrastructure.persistence.adapter;

import com.uqdelivery.backend.domain.model.Direccion;
import com.uqdelivery.backend.domain.model.Envio;
import com.uqdelivery.backend.domain.model.EstadoEnvio;
import com.uqdelivery.backend.domain.model.Paquete;
import com.uqdelivery.backend.domain.model.Receptor;
import com.uqdelivery.backend.infrastructure.persistence.entity.EnvioEntity;
import com.uqdelivery.backend.infrastructure.persistence.repository.EnvioJpaRepository;
import com.uqdelivery.backend.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnvioRepositoryAdapterTest {

    @Test
    void saveLeavesGeneratedIdUnsetAndReturnsPersistedId() {
        EnvioJpaRepository envioJpaRepository = mock(EnvioJpaRepository.class);
        UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);
        EnvioRepositoryAdapter adapter = new EnvioRepositoryAdapter(envioJpaRepository, userJpaRepository);
        UUID persistedId = UUID.randomUUID();

        when(envioJpaRepository.save(any(EnvioEntity.class))).thenAnswer(invocation -> {
            EnvioEntity entity = invocation.getArgument(0);
            assertNull(entity.getId());
            entity.setId(persistedId);
            return entity;
        });

        Envio saved = adapter.save(new Envio(
                UUID.randomUUID(),
                "UQD-1234ABCD",
                null,
                new Paquete("Libros", 2.5),
                new Direccion("Calle 1", "Armenia", null),
                new Direccion("Calle 2", "Pereira", null),
                new Receptor("Ana", "3001234567", null),
                EstadoEnvio.CREADO,
                LocalDateTime.now()
        ));

        assertEquals(persistedId, saved.getId());
        verify(envioJpaRepository).save(any(EnvioEntity.class));
    }
}
