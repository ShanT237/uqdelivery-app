package com.uqdelivery.backend.domain.port.in;

import com.uqdelivery.backend.application.dto.CrearEnvioRequestDto;
import com.uqdelivery.backend.application.dto.EnvioResponseDto;

/**
 * Puerto de entrada para el caso de uso de creación de envíos.
 */
public interface CrearEnvioUseCase {

    EnvioResponseDto crear(CrearEnvioRequestDto request);
}
