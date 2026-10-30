package com.uqdelivery.backend.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CrearEnvioRequestDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CrearEnvioRequestDto crearDtoBaseValido() {
        CrearEnvioRequestDto.PaqueteDto paquete = new CrearEnvioRequestDto.PaqueteDto("Paquete de prueba", 5.0);
        CrearEnvioRequestDto.DireccionDto origen = new CrearEnvioRequestDto.DireccionDto("Calle 10", "Armenia", "Casa 1");
        CrearEnvioRequestDto.DireccionDto destino = new CrearEnvioRequestDto.DireccionDto("Carrera 20", "Pereira", "Oficina 302");
        CrearEnvioRequestDto.ReceptorDto receptor = new CrearEnvioRequestDto.ReceptorDto("Pedro Perez", "3119876543", "pedro@test.com");
        return new CrearEnvioRequestDto(paquete, origen, destino, receptor);
    }

    @Test
    @DisplayName("DTO válido no debe tener ninguna violación de validación")
    void dtoValido_SinViolaciones() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertTrue(violaciones.isEmpty(), "Un DTO con datos válidos no debe generar violaciones");
    }

    @Test
    @DisplayName("Peso inferior a 0.1 kg debe fallar la validación")
    void pesoMenorAlMinimo_DebeFallar() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        dto.getPaquete().setPesoKg(0.05);

        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("paquete.pesoKg")));
    }

    @Test
    @DisplayName("Peso superior a 50 kg debe fallar la validación")
    void pesoMayorAlMaximo_DebeFallar() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        dto.getPaquete().setPesoKg(50.1);

        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("paquete.pesoKg")));
    }

    @Test
    @DisplayName("Teléfono con formato incorrecto debe fallar la validación")
    void telefonoInvalido_DebeFallar() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        dto.getReceptor().setTelefono("12345"); // Menos de 7 dígitos

        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("receptor.telefono")));

        dto.getReceptor().setTelefono("abcdefghij"); // No numérico
        violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("receptor.telefono")));
    }

    @Test
    @DisplayName("Campos con texto superior a 255 caracteres deben fallar la validación")
    void textoMasDe255Caracteres_DebeFallar() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        String textoLargo = "A".repeat(256);

        dto.getOrigen().setCalle(textoLargo);
        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("origen.calle")));

        dto = crearDtoBaseValido();
        dto.getReceptor().setNombre(textoLargo);
        violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("receptor.nombre")));
    }

    @Test
    @DisplayName("Descripción de paquete con más de 200 caracteres debe fallar")
    void descripcionMasDe200Caracteres_DebeFallar() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        dto.getPaquete().setDescripcion("B".repeat(201));

        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("paquete.descripcion")));
    }

    @Test
    @DisplayName("Email con formato inválido debe fallar")
    void emailInvalido_DebeFallar() {
        CrearEnvioRequestDto dto = crearDtoBaseValido();
        dto.getReceptor().setEmail("formato-invalido-sin-arroba");

        Set<ConstraintViolation<CrearEnvioRequestDto>> violaciones = validator.validate(dto);
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().contains("receptor.email")));
    }
}
