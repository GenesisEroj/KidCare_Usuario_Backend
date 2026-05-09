package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada para el endpoint POST /api/auth/restablecer.
 *
 * <p>{@code token} es el UUID enviado al correo del usuario; se invalida tras usarse.
 */
@Data
public class NuevaPasswordRequestDTO {

    // Token de recuperación enviado por correo
    @NotBlank(message = "El token es obligatorio")
    private String token;

    // Nueva contraseña del usuario
    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaPassword;
}