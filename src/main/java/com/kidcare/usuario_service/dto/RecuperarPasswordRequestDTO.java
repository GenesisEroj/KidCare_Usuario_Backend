package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO de entrada para el endpoint POST /api/auth/recuperar. */
@Data
public class RecuperarPasswordRequestDTO {

    // Correo electrónico del usuario que olvidó su contraseña
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;
}