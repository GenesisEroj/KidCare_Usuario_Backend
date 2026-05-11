package com.kidcare.usuario_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditoriaResponseDTO {
    private Integer idAuditoria;
    private String emailAdmin;
    private String cambio;
    private String entidad;
    private Integer idEntidad;
    private LocalDate fecha;
}
