package com.uqdelivery.backend.domain.port.in;

import com.uqdelivery.backend.application.dto.AuthResponseDto;
import com.uqdelivery.backend.application.dto.LoginRequestDto;

/**
 * Puerto de entrada (use case) para el inicio de sesión de usuarios.
 */
public interface LoginUserUseCase {

    AuthResponseDto login(LoginRequestDto request);
}
