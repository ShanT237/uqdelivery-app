package com.uqdelivery.backend.infrastructure.web.controller;

import com.uqdelivery.backend.application.dto.AuthResponseDto;
import com.uqdelivery.backend.domain.port.in.LoginUserUseCase;
import com.uqdelivery.backend.domain.port.in.RegisterUserUseCase;
import com.uqdelivery.backend.infrastructure.security.JwtAuthenticationFilter;
import com.uqdelivery.backend.infrastructure.security.JwtTokenProvider;
import com.uqdelivery.backend.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, JwtTokenProvider.class, UserDetailsServiceImpl.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    void registerReturnsAuthenticationResponse() throws Exception {
        when(registerUserUseCase.register(any())).thenReturn(
                new AuthResponseDto("jwt", "ana@example.com", "Ana", "USER"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Ana","email":"ana@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.email").value("ana@example.com"));

        verify(registerUserUseCase).register(any());
    }

    @Test
    void registerRejectsPasswordLongerThan72Characters() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Ana","email":"ana@example.com","password":"%s"}
                                """.formatted("x".repeat(73))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void loginReturnsAuthenticationResponse() throws Exception {
        when(loginUserUseCase.login(any())).thenReturn(
                new AuthResponseDto("jwt", "ana@example.com", "Ana", "USER"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.email").value("ana@example.com"));

        verify(loginUserUseCase).login(any());
    }
}
