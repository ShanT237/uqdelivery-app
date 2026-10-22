package com.uqdelivery.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa los roles de usuario en la base de datos.
 */
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name; // Ej: "USER", "ADMIN"

    public RoleEntity() {}

    public RoleEntity(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
