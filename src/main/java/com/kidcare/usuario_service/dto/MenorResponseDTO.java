package com.kidcare.usuario_service.dto;

import lombok.Data;
import java.time.LocalDate;

/** DTO de respuesta para los endpoints GET, POST y PUT /api/menores. */
@Data
public class MenorResponseDTO {

    // Identificador único del menor
    private Integer idMenor;

    // Nombre completo del menor
    private String nombre;

    // Fecha de nacimiento del menor
    private LocalDate fechaNacimiento;

    // Sexo del menor
    private String sexo;

    // Emoji representativo (puede ser null)
    private String emoji;

    // true si el usuario autenticado es delegado de este menor (no es el propietario)
    private boolean esDelegado;
}