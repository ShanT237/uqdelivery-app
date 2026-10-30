package com.uqdelivery.backend.domain.model;

/**
 * Modelo de dominio para Paquete.
 */
public class Paquete {
    private String descripcion;
    private double pesoKg;

    public Paquete() {}

    public Paquete(String descripcion, double pesoKg) {
        this.descripcion = descripcion;
        this.pesoKg = pesoKg;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }
}
