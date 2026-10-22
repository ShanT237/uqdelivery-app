package com.uqdelivery.backend.application.dto;

/**
 * DTO de respuesta para los endpoints de autenticación.
 * Contiene el token JWT y datos básicos del usuario autenticado.
 */
public class AuthResponseDto {

    private String token;
    private String email;
    private String fullName;
    private String role;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, String email, String fullName, String role) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
