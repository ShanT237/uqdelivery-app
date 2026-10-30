package com.uqdelivery.backend.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la tabla 'envios' en PostgreSQL.
 * Posee relación con la entidad PaqueteEntity y con UserEntity (emisor).
 */
@Entity
@Table(name = "envios")
public class EnvioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigoSeguimiento;

    // ─── Relación con Usuario Emisor (Nullable según requerimiento) ───────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = true)
    private UserEntity emisor;

    // ─── Relación con Entidad Paquete ─────────────────────────────────────────
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "paquete_id", nullable = false)
    private PaqueteEntity paquete;

    // ─── Dirección de origen ─────────────────────────────────────────────────
    @Column(nullable = false)
    private String origenCalle;

    @Column(nullable = false)
    private String origenCiudad;

    @Column
    private String origenReferencia;

    // ─── Dirección de destino ────────────────────────────────────────────────
    @Column(nullable = false)
    private String destinoCalle;

    @Column(nullable = false)
    private String destinoCiudad;

    @Column
    private String destinoReferencia;

    // ─── Datos del receptor ──────────────────────────────────────────────────
    @Column(nullable = false)
    private String receptorNombre;

    @Column(nullable = false)
    private String receptorTelefono;

    @Column
    private String receptorEmail;

    // ─── Estado y trazabilidad ───────────────────────────────────────────────
    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    public EnvioEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }

    public UserEntity getEmisor() { return emisor; }
    public void setEmisor(UserEntity emisor) { this.emisor = emisor; }

    public PaqueteEntity getPaquete() { return paquete; }
    public void setPaquete(PaqueteEntity paquete) { this.paquete = paquete; }

    public String getOrigenCalle() { return origenCalle; }
    public void setOrigenCalle(String origenCalle) { this.origenCalle = origenCalle; }

    public String getOrigenCiudad() { return origenCiudad; }
    public void setOrigenCiudad(String origenCiudad) { this.origenCiudad = origenCiudad; }

    public String getOrigenReferencia() { return origenReferencia; }
    public void setOrigenReferencia(String origenReferencia) { this.origenReferencia = origenReferencia; }

    public String getDestinoCalle() { return destinoCalle; }
    public void setDestinoCalle(String destinoCalle) { this.destinoCalle = destinoCalle; }

    public String getDestinoCiudad() { return destinoCiudad; }
    public void setDestinoCiudad(String destinoCiudad) { this.destinoCiudad = destinoCiudad; }

    public String getDestinoReferencia() { return destinoReferencia; }
    public void setDestinoReferencia(String destinoReferencia) { this.destinoReferencia = destinoReferencia; }

    public String getReceptorNombre() { return receptorNombre; }
    public void setReceptorNombre(String receptorNombre) { this.receptorNombre = receptorNombre; }

    public String getReceptorTelefono() { return receptorTelefono; }
    public void setReceptorTelefono(String receptorTelefono) { this.receptorTelefono = receptorTelefono; }

    public String getReceptorEmail() { return receptorEmail; }
    public void setReceptorEmail(String receptorEmail) { this.receptorEmail = receptorEmail; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
