package com.uqdelivery.backend.domain.model;

/**
 * Modelo de dominio para Receptor del envío.
 */
public class Receptor {
    private String nombre;
    private String telefono;
    private String email;

    public Receptor() {}

    public Receptor(String nombre, String telefono, String email) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
