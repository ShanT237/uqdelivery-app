package com.uqdelivery.backend.application.dto;

/**
 * DTO de respuesta para el frontend tras la creación de un envío.
 * Coincide exactamente con la interfaz EnvioResponse del frontend Angular.
 */
public class EnvioResponseDto {

    private String id;
    private String codigoSeguimiento;
    private String estado;

    public EnvioResponseDto() {}

    public EnvioResponseDto(String id, String codigoSeguimiento, String estado) {
        this.id = id;
        this.codigoSeguimiento = codigoSeguimiento;
        this.estado = estado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
