package com.kidcare.usuario_service.dto;

import lombok.Data;

/** DTO de entrada para el endpoint POST /api/delegados/vincular. */
@Data
public class VincularDelegadoRequestDTO {
    /** Correo del usuario a vincular. */
    private String emailDelegado;
    /** ID del menor al que se otorga acceso. */
    private Integer idMenor;
    /** Fecha de expiración en formato YYYY-MM-DD; null para acceso permanente. */
    private String fechaExpiracion;
}
