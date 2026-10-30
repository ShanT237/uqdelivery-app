package com.uqdelivery.backend.domain.model;

/**
 * Objeto de valor/modelo de dominio para Dirección.
 */
public class Direccion {
    private String calle;
    private String ciudad;
    private String referencia;

    public Direccion() {}

    public Direccion(String calle, String ciudad, String referencia) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.referencia = referencia;
    }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}
