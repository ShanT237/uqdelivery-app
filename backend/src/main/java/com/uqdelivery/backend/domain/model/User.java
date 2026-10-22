package com.uqdelivery.backend.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio pura — sin dependencias de frameworks.
 * Representa un usuario del sistema.
 */
public class User {

    private UUID id;
    private String fullName;
    private String email;
    private String password;   // almacenado como hash BCrypt
    private Role role;
    private LocalDateTime createdAt;

    public User() {}

    public User(UUID id, String fullName, String email, String password, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
