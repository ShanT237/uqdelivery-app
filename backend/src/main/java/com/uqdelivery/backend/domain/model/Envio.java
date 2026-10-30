package com.uqdelivery.backend.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio pura para Envio.
 */
public class Envio {
    private UUID id;
    private String codigoSeguimiento;
    private String emisorEmail;
    private Paquete paquete;
    private Direccion origen;
    private Direccion destino;
    private Receptor receptor;
    private EstadoEnvio estado;
    private LocalDateTime creadoEn;

    public Envio() {}

    public Envio(UUID id, String codigoSeguimiento, String emisorEmail, Paquete paquete,
                 Direccion origen, Direccion destino, Receptor receptor,
                 EstadoEnvio estado, LocalDateTime creadoEn) {
        this.id = id;
        this.codigoSeguimiento = codigoSeguimiento;
        this.emisorEmail = emisorEmail;
        this.paquete = paquete;
        this.origen = origen;
        this.destino = destino;
        this.receptor = receptor;
        this.estado = estado;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }

    public String getEmisorEmail() { return emisorEmail; }
    public void setEmisorEmail(String emisorEmail) { this.emisorEmail = emisorEmail; }

    public Paquete getPaquete() { return paquete; }
    public void setPaquete(Paquete paquete) { this.paquete = paquete; }

    public Direccion getOrigen() { return origen; }
    public void setOrigen(Direccion origen) { this.origen = origen; }

    public Direccion getDestino() { return destino; }
    public void setDestino(Direccion destino) { this.destino = destino; }

    public Receptor getReceptor() { return receptor; }
    public void setReceptor(Receptor receptor) { this.receptor = receptor; }

    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
