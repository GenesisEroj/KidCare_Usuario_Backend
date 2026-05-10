package com.kidcare.usuario_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminUsuarioResponseDTO {
    private Integer idUsuario;
    private String nombreCompleto;
    private String email;
    private String rol;
    private Boolean activo;
    private LocalDate fechaCreacion;
}
