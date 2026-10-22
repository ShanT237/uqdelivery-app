package com.uqdelivery.backend.infrastructure.web.controller;

import com.uqdelivery.backend.application.dto.AuthResponseDto;
import com.uqdelivery.backend.application.dto.LoginRequestDto;
import com.uqdelivery.backend.application.dto.RegisterRequestDto;
import com.uqdelivery.backend.domain.port.in.LoginUserUseCase;
import com.uqdelivery.backend.domain.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para los endpoints de autenticación.
 * Expone POST /api/v1/auth/register y POST /api/v1/auth/login.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request datos de registro (fullName, email, password)
     * @return 200 OK con token JWT y datos del usuario
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = registerUserUseCase.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Autentica un usuario existente.
     *
     * @param request credenciales (email, password)
     * @return 200 OK con token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = loginUserUseCase.login(request);
        return ResponseEntity.ok(response);
    }
}
