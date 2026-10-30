package com.uqdelivery.backend.application.service;

import com.uqdelivery.backend.application.dto.CrearEnvioRequestDto;
import com.uqdelivery.backend.application.dto.EnvioResponseDto;
import com.uqdelivery.backend.domain.model.Envio;
import com.uqdelivery.backend.domain.model.EstadoEnvio;
import com.uqdelivery.backend.domain.model.Role;
import com.uqdelivery.backend.domain.model.User;
import com.uqdelivery.backend.domain.port.out.EnvioRepositoryPort;
import com.uqdelivery.backend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepositoryPort envioRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private EnvioService envioService;

    private CrearEnvioRequestDto requestValido;
    private User usuarioMock;

    @BeforeEach
    void setUp() {
        CrearEnvioRequestDto.PaqueteDto paquete = new CrearEnvioRequestDto.PaqueteDto("Documentos universitarios", 2.5);
        CrearEnvioRequestDto.DireccionDto origen = new CrearEnvioRequestDto.DireccionDto("Calle 12 # 34", "Armenia", "Bloque 2");
        CrearEnvioRequestDto.DireccionDto destino = new CrearEnvioRequestDto.DireccionDto("Carrera 15 # 20", "Armenia", "Apto 101");
        CrearEnvioRequestDto.ReceptorDto receptor = new CrearEnvioRequestDto.ReceptorDto("Carlos Gomez", "3001234567", "carlos@example.com");

        requestValido = new CrearEnvioRequestDto(paquete, origen, destino, receptor);

        usuarioMock = new User(
                UUID.randomUUID(),
                "Juan Perez",
                "juan@example.com",
                "passwordHash",
                Role.USER,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Debe crear un envío con estado CREADO y código con formato UQD-XXXXXXXX")
    void crearEnvio_Exitoso() {
        // Arrange
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioMock));
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EnvioResponseDto respuesta = envioService.crear(requestValido, "juan@example.com");

        // Assert
        assertNotNull(respuesta);
        assertNotNull(respuesta.getId());
        assertEquals(EstadoEnvio.CREADO.name(), respuesta.getEstado());

        // Verificar formato UQD-XXXXXXXX (UQD- seguido de 8 caracteres alfanuméricos)
        assertNotNull(respuesta.getCodigoSeguimiento());
        assertTrue(respuesta.getCodigoSeguimiento().matches("^UQD-[A-F0-9]{8}$"),
                "El código de seguimiento debe cumplir con el formato UQD-XXXXXXXX: " + respuesta.getCodigoSeguimiento());

        // Verificar persistencia y asignación del emisor
        ArgumentCaptor<Envio> envioCaptor = ArgumentCaptor.forClass(Envio.class);
        verify(envioRepository, times(1)).save(envioCaptor.capture());
        Envio envioGuardado = envioCaptor.getValue();
        assertEquals("juan@example.com", envioGuardado.getEmisorEmail());
        assertEquals(EstadoEnvio.CREADO, envioGuardado.getEstado());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario emisor no existe")
    void crearEnvio_UsuarioNoEncontrado() {
        // Arrange
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                envioService.crear(requestValido, "noexiste@example.com")
        );
        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(envioRepository, never()).save(any(Envio.class));
    }
}
