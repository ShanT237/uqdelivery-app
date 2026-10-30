package com.uqdelivery.backend.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la solicitud de creación de un envío.
 * Coincide exactamente con la estructura JSON enviada desde Angular.
 */
public class CrearEnvioRequestDto {

    @Valid
    @NotNull(message = "Los datos del paquete son obligatorios")
    private PaqueteDto paquete;

    @Valid
    @NotNull(message = "La dirección de origen es obligatoria")
    private DireccionDto origen;

    @Valid
    @NotNull(message = "La dirección de destino es obligatoria")
    private DireccionDto destino;

    @Valid
    @NotNull(message = "Los datos del receptor son obligatorios")
    private ReceptorDto receptor;

    public CrearEnvioRequestDto() {}

    public CrearEnvioRequestDto(PaqueteDto paquete, DireccionDto origen, DireccionDto destino, ReceptorDto receptor) {
        this.paquete = paquete;
        this.origen = origen;
        this.destino = destino;
        this.receptor = receptor;
    }

    public PaqueteDto getPaquete() { return paquete; }
    public void setPaquete(PaqueteDto paquete) { this.paquete = paquete; }

    public DireccionDto getOrigen() { return origen; }
    public void setOrigen(DireccionDto origen) { this.origen = origen; }

    public DireccionDto getDestino() { return destino; }
    public void setDestino(DireccionDto destino) { this.destino = destino; }

    public ReceptorDto getReceptor() { return receptor; }
    public void setReceptor(ReceptorDto receptor) { this.receptor = receptor; }

    // ─── Sub-DTOs ────────────────────────────────────────────────────────────

    public static class PaqueteDto {
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 200, message = "La descripción no debe superar 200 caracteres")
        private String descripcion;

        @NotNull(message = "El peso es obligatorio")
        @DecimalMin(value = "0.1", message = "El peso mínimo es 0.1 kg")
        @DecimalMax(value = "50.0", message = "El peso máximo es 50 kg")
        private Double pesoKg;

        public PaqueteDto() {}

        public PaqueteDto(String descripcion, Double pesoKg) {
            this.descripcion = descripcion;
            this.pesoKg = pesoKg;
        }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public Double getPesoKg() { return pesoKg; }
        public void setPesoKg(Double pesoKg) { this.pesoKg = pesoKg; }
    }

    public static class DireccionDto {
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 255, message = "La calle no debe superar 255 caracteres")
        private String calle;

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 255, message = "La ciudad no debe superar 255 caracteres")
        private String ciudad;

        @Size(max = 255, message = "La referencia no debe superar 255 caracteres")
        private String referencia;

        public DireccionDto() {}

        public DireccionDto(String calle, String ciudad, String referencia) {
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

    public static class ReceptorDto {
        @NotBlank(message = "El nombre del receptor es obligatorio")
        @Size(max = 255, message = "El nombre del receptor no debe superar 255 caracteres")
        private String nombre;

        @NotBlank(message = "El teléfono del receptor es obligatorio")
        @Pattern(regexp = "^\\d{7,10}$", message = "El teléfono debe tener entre 7 y 10 dígitos")
        private String telefono;

        @Size(max = 255, message = "El correo electrónico no debe superar 255 caracteres")
        @Email(message = "El correo electrónico no es válido")
        private String email;

        public ReceptorDto() {}

        public ReceptorDto(String nombre, String telefono, String email) {
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
}
