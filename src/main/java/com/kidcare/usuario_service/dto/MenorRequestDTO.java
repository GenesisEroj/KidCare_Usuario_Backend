package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/** DTO de entrada para los endpoints POST y PUT /api/menores. */
@Data
public class MenorRequestDTO {

    // Nombre completo del menor
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Fecha de nacimiento del menor
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    // Sexo del menor
    @NotBlank(message = "El sexo es obligatorio")
    private String sexo;

    // Emoji representativo (opcional)
    private String emoji;
}