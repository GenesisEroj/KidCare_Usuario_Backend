package com.kidcare.usuario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta para las operaciones de login y registro.
 *
 * <p>Retorna el token JWT listo para usar, el email del usuario y su rol,
 * de forma que el cliente Android pueda persistirlos en SessionManager.
 */
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