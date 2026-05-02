package com.kidcare.usuario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO que retorna el token JWT y datos básicos del usuario autenticado
@Data
@AllArgsConstructor
public class AuthResponseDTO {

    // Token JWT generado al autenticarse
    private String token;

    // Correo del usuario autenticado
    private String email;

    // Rol del usuario autenticado
    private String rol;
}