package com.uqdelivery.backend.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA que representa la tabla 'paquetes' en PostgreSQL.
 */
@Entity
@Table(name = "paquetes")
public class PaqueteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false)
    private double pesoKg;

    public PaqueteEntity() {}

    public PaqueteEntity(String descripcion, double pesoKg) {
        this.descripcion = descripcion;
        this.pesoKg = pesoKg;
    }

    public PaqueteEntity(UUID id, String descripcion, double pesoKg) {
        this.id = id;
        this.descripcion = descripcion;
        this.pesoKg = pesoKg;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        this.pesoKg = pesoKg;
    }
}
