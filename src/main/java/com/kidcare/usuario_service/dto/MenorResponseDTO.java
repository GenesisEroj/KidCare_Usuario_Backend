package com.kidcare.usuario_service.dto;

import lombok.Data;
import java.time.LocalDate;

// DTO que retorna los datos de un perfil de menor
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
}