package com.uqdelivery.backend.application.service;

import com.uqdelivery.backend.application.dto.AuthResponseDto;
import com.uqdelivery.backend.application.dto.LoginRequestDto;
import com.uqdelivery.backend.application.dto.RegisterRequestDto;
import com.uqdelivery.backend.domain.model.Role;
import com.uqdelivery.backend.domain.model.User;
import com.uqdelivery.backend.domain.port.in.LoginUserUseCase;
import com.uqdelivery.backend.domain.port.in.RegisterUserUseCase;
import com.uqdelivery.backend.domain.port.out.UserRepositoryPort;
import com.uqdelivery.backend.infrastructure.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de aplicación que implementa los casos de uso de autenticación.
 * Orquesta la lógica de registro y login sin depender de frameworks web.
 */
@Service
public class AuthService implements RegisterUserUseCase, LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepositoryPort userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ─── Registro ────────────────────────────────────────────────────────────

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Ya existe un usuario registrado con el email: " + request.getEmail());
        }

        User newUser = new User(
                UUID.randomUUID(),
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER,
                LocalDateTime.now()
        );

        User savedUser = userRepository.save(newUser);
        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponseDto(token, savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole().name());
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDto(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
