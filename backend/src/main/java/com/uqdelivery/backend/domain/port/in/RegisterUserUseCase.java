package com.uqdelivery.backend.domain.port.in;

import com.uqdelivery.backend.application.dto.AuthResponseDto;
import com.uqdelivery.backend.application.dto.RegisterRequestDto;

/**
 * Puerto de entrada (use case) para el registro de nuevos usuarios.
 */
public interface RegisterUserUseCase {

    AuthResponseDto register(RegisterRequestDto request);
}
