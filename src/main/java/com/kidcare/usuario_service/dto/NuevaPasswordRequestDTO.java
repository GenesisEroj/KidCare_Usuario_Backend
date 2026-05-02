package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO que recibe el token y la nueva contraseña para restablecerla
@Data
public class NuevaPasswordRequestDTO {

    // Token de recuperación enviado por correo
    @NotBlank(message = "El token es obligatorio")
    private String token;

    // Nueva contraseña del usuario
    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaPassword;
}