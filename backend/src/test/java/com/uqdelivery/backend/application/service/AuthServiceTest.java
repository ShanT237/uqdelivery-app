package com.uqdelivery.backend.application.service;

import com.uqdelivery.backend.application.dto.AuthResponseDto;
import com.uqdelivery.backend.application.dto.LoginRequestDto;
import com.uqdelivery.backend.application.dto.RegisterRequestDto;
import com.uqdelivery.backend.domain.model.Role;
import com.uqdelivery.backend.domain.model.User;
import com.uqdelivery.backend.domain.port.out.UserRepositoryPort;
import com.uqdelivery.backend.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_normalizesEmailAndPersistsNormalizedAddress() {
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenProvider.generateToken("ana@example.com", "USER")).thenReturn("jwt");

        AuthResponseDto response = authService.register(
                new RegisterRequestDto("Ana", " Ana@Example.COM ", "password123"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("ana@example.com", userCaptor.getValue().getEmail());
        assertEquals("ana@example.com", response.getEmail());
        assertEquals("jwt", response.getToken());
    }

    @Test
    void registerRejectsEmailAlreadyRegisteredRegardlessOfCase() {
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> authService.register(
                new RegisterRequestDto("Ana", "ANA@EXAMPLE.COM", "password123")));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginNormalizesEmailBeforeLookup() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@example.com", "encoded-password",
                Role.USER, LocalDateTime.now());
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("ana@example.com", "USER")).thenReturn("jwt");

        AuthResponseDto response = authService.login(new LoginRequestDto("ANA@Example.com", "password123"));

        assertEquals("ana@example.com", response.getEmail());
        assertEquals("jwt", response.getToken());
        verify(userRepository).findByEmail("ana@example.com");
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@example.com", "encoded-password",
                Role.USER, LocalDateTime.now());
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrecta", "encoded-password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                authService.login(new LoginRequestDto("ana@example.com", "incorrecta")));
        verify(jwtTokenProvider, never()).generateToken(any(), any());
    }
}
