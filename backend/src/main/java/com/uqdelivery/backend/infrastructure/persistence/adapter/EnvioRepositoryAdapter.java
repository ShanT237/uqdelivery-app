package com.uqdelivery.backend.infrastructure.persistence.adapter;

import com.uqdelivery.backend.domain.model.Direccion;
import com.uqdelivery.backend.domain.model.Envio;
import com.uqdelivery.backend.domain.model.EstadoEnvio;
import com.uqdelivery.backend.domain.model.Paquete;
import com.uqdelivery.backend.domain.model.Receptor;
import com.uqdelivery.backend.domain.port.out.EnvioRepositoryPort;
import com.uqdelivery.backend.infrastructure.persistence.entity.EnvioEntity;
import com.uqdelivery.backend.infrastructure.persistence.entity.PaqueteEntity;
import com.uqdelivery.backend.infrastructure.persistence.repository.EnvioJpaRepository;
import com.uqdelivery.backend.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia que conecta el puerto EnvioRepositoryPort con JPA.
 */
@Component
public class EnvioRepositoryAdapter implements EnvioRepositoryPort {

    private final EnvioJpaRepository envioJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public EnvioRepositoryAdapter(EnvioJpaRepository envioJpaRepository, UserJpaRepository userJpaRepository) {
        this.envioJpaRepository = envioJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Envio save(Envio envio) {
        EnvioEntity entity = toEntity(envio);
        EnvioEntity guardado = envioJpaRepository.save(entity);
        return toDomain(guardado);
    }

    private EnvioEntity toEntity(Envio envio) {
        EnvioEntity entity = new EnvioEntity();
        entity.setId(envio.getId());
        entity.setCodigoSeguimiento(envio.getCodigoSeguimiento());

        // Asociar emisor_id si existe
        if (envio.getEmisorEmail() != null && !envio.getEmisorEmail().isBlank()) {
            userJpaRepository.findByEmail(envio.getEmisorEmail())
                    .ifPresent(entity::setEmisor);
        }

        // Mapeo a PaqueteEntity
        PaqueteEntity paqueteEntity = new PaqueteEntity(
                envio.getPaquete().getDescripcion(),
                envio.getPaquete().getPesoKg()
        );
        entity.setPaquete(paqueteEntity);

        entity.setOrigenCalle(envio.getOrigen().getCalle());
        entity.setOrigenCiudad(envio.getOrigen().getCiudad());
        entity.setOrigenReferencia(envio.getOrigen().getReferencia());
        entity.setDestinoCalle(envio.getDestino().getCalle());
        entity.setDestinoCiudad(envio.getDestino().getCiudad());
        entity.setDestinoReferencia(envio.getDestino().getReferencia());
        entity.setReceptorNombre(envio.getReceptor().getNombre());
        entity.setReceptorTelefono(envio.getReceptor().getTelefono());
        entity.setReceptorEmail(envio.getReceptor().getEmail());
        entity.setEstado(envio.getEstado().name());
        entity.setCreadoEn(envio.getCreadoEn());
        return entity;
    }

    private Envio toDomain(EnvioEntity entity) {
        String emisorEmail = entity.getEmisor() != null ? entity.getEmisor().getEmail() : null;
        return new Envio(
                entity.getId(),
                entity.getCodigoSeguimiento(),
                emisorEmail,
                new Paquete(entity.getPaquete().getDescripcion(), entity.getPaquete().getPesoKg()),
                new Direccion(entity.getOrigenCalle(), entity.getOrigenCiudad(), entity.getOrigenReferencia()),
                new Direccion(entity.getDestinoCalle(), entity.getDestinoCiudad(), entity.getDestinoReferencia()),
                new Receptor(entity.getReceptorNombre(), entity.getReceptorTelefono(), entity.getReceptorEmail()),
                EstadoEnvio.valueOf(entity.getEstado()),
                entity.getCreadoEn()
        );
    }
}
