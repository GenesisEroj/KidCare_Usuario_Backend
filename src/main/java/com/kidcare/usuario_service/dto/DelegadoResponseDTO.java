package com.kidcare.usuario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** DTO de salida para GET /api/delegados/menor/{idMenor}. */
@Data
@AllArgsConstructor
public class DelegadoResponseDTO {
    private Integer idUsuario;
    private String email;
    private String nombreCompleto;
    /** Fecha de expiración en formato YYYY-MM-DD; null si el acceso es permanente. */
    private String fechaExpiracion;
}
