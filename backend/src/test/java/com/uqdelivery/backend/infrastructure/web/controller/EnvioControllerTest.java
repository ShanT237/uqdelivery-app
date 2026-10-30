package com.uqdelivery.backend.infrastructure.web.controller;

import com.uqdelivery.backend.application.dto.CrearEnvioRequestDto;
import com.uqdelivery.backend.application.dto.EnvioResponseDto;
import com.uqdelivery.backend.domain.port.in.CrearEnvioUseCase;
import com.uqdelivery.backend.infrastructure.security.JwtAuthenticationFilter;
import com.uqdelivery.backend.infrastructure.security.JwtTokenProvider;
import com.uqdelivery.backend.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = EnvioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, JwtTokenProvider.class, UserDetailsServiceImpl.class}
        )
)
@AutoConfigureMockMvc
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearEnvioUseCase crearEnvioUseCase;

    private final String jsonValido = """
            {
              "paquete": {
                "descripcion": "Caja con libros",
                "pesoKg": 4.5
              },
              "origen": {
                "calle": "Calle 10 # 14-20",
                "ciudad": "Armenia",
                "referencia": "Casa esquinera"
              },
              "destino": {
                "calle": "Carrera 15 # 8-30",
                "ciudad": "Pereira",
                "referencia": "Apto 301"
              },
              "receptor": {
                "nombre": "Ana Maria Lopez",
                "telefono": "3154879621",
                "email": "ana@correo.com"
              }
            }
            """;

    private final String jsonInvalidoPesoNegativo = """
            {
              "paquete": {
                "descripcion": "Caja",
                "pesoKg": -1.0
              },
              "origen": {
                "calle": "Calle 1",
                "ciudad": "Armenia"
              },
              "destino": {
                "calle": "Calle 2",
                "ciudad": "Pereira"
              },
              "receptor": {
                "nombre": "Pedro",
                "telefono": "123"
              }
            }
            """;

    @Test
    @WithMockUser(username = "usuario@uqdelivery.com", roles = {"USER"})
    @DisplayName("POST /api/v1/envios con datos válidos y usuario autenticado debe retornar 201 Created")
    void crearEnvio_Valido_Retorna201() throws Exception {
        EnvioResponseDto mockRespuesta = new EnvioResponseDto(
                "d290f1ee-6c54-4b01-90e6-d701748f0851",
                "UQD-A1B2C3D4",
                "CREADO"
        );

        when(crearEnvioUseCase.crear(any(CrearEnvioRequestDto.class), eq("usuario@uqdelivery.com")))
                .thenReturn(mockRespuesta);

        mockMvc.perform(post("/api/v1/envios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("d290f1ee-6c54-4b01-90e6-d701748f0851"))
                .andExpect(jsonPath("$.codigoSeguimiento").value("UQD-A1B2C3D4"))
                .andExpect(jsonPath("$.estado").value("CREADO"));
    }

    @Test
    @WithMockUser(username = "usuario@uqdelivery.com", roles = {"USER"})
    @DisplayName("POST /api/v1/envios con datos inválidos debe retornar 400 Bad Request")
    void crearEnvio_Invalido_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/envios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalidoPesoNegativo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada inválidos"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/v1/envios sin autenticación debe retornar 401 o 403")
    void crearEnvio_SinAutenticacion_RetornaNoAutorizado() throws Exception {
        mockMvc.perform(post("/api/v1/envios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isForbidden());
    }
}
