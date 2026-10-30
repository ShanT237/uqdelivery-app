package com.uqdelivery.backend.infrastructure.web.controller;

import com.uqdelivery.backend.application.dto.CrearEnvioRequestDto;
import com.uqdelivery.backend.application.dto.EnvioResponseDto;
import com.uqdelivery.backend.domain.port.in.CrearEnvioUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de envíos (HU-04).
 * Expone POST /api/v1/envios.
 */
@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    private final CrearEnvioUseCase crearEnvioUseCase;

    public EnvioController(CrearEnvioUseCase crearEnvioUseCase) {
        this.crearEnvioUseCase = crearEnvioUseCase;
    }

    /**
     * Endpoint para registrar una nueva solicitud de envío asociada al usuario autenticado.
     *
     * @param request datos completos del paquete, origen, destino y receptor.
     * @param authentication contexto de seguridad del usuario autenticado.
     * @return 201 Created con el ID, código de seguimiento y estado del envío.
     */
    @PostMapping
    public ResponseEntity<EnvioResponseDto> crear(
            @Valid @RequestBody CrearEnvioRequestDto request,
            Authentication authentication) {
        String emailUsuario = authentication != null ? authentication.getName() : null;
        EnvioResponseDto response = crearEnvioUseCase.crear(request, emailUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
