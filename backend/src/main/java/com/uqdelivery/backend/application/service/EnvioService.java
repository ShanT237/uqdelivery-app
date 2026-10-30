package com.uqdelivery.backend.application.service;

import com.uqdelivery.backend.application.dto.CrearEnvioRequestDto;
import com.uqdelivery.backend.application.dto.EnvioResponseDto;
import com.uqdelivery.backend.domain.model.Direccion;
import com.uqdelivery.backend.domain.model.Envio;
import com.uqdelivery.backend.domain.model.EstadoEnvio;
import com.uqdelivery.backend.domain.model.Paquete;
import com.uqdelivery.backend.domain.model.Receptor;
import com.uqdelivery.backend.domain.port.in.CrearEnvioUseCase;
import com.uqdelivery.backend.domain.port.out.EnvioRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de aplicación que implementa el caso de uso CrearEnvioUseCase.
 */
@Service
public class EnvioService implements CrearEnvioUseCase {

    private final EnvioRepositoryPort envioRepository;

    public EnvioService(EnvioRepositoryPort envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    public EnvioResponseDto crear(CrearEnvioRequestDto request) {
        // 1. Generación de código único de seguimiento (formato UQD-XXXXXXXX)
        String codigoSeguimiento = "UQD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Mapeo del DTO al modelo de dominio puro
        Paquete paquete = new Paquete(
                request.getPaquete().getDescripcion(),
                request.getPaquete().getPesoKg()
        );

        Direccion origen = new Direccion(
                request.getOrigen().getCalle(),
                request.getOrigen().getCiudad(),
                request.getOrigen().getReferencia()
        );

        Direccion destino = new Direccion(
                request.getDestino().getCalle(),
                request.getDestino().getCiudad(),
                request.getDestino().getReferencia()
        );

        Receptor receptor = new Receptor(
                request.getReceptor().getNombre(),
                request.getReceptor().getTelefono(),
                request.getReceptor().getEmail()
        );

        Envio envio = new Envio(
                UUID.randomUUID(),
                codigoSeguimiento,
                paquete,
                origen,
                destino,
                receptor,
                EstadoEnvio.CREADO,
                LocalDateTime.now()
        );

        // 3. Guardado a través del puerto de persistencia
        Envio guardado = envioRepository.save(envio);

        // 4. Retorno del DTO de respuesta
        return new EnvioResponseDto(
                guardado.getId().toString(),
                guardado.getCodigoSeguimiento(),
                guardado.getEstado().name()
        );
    }
}
