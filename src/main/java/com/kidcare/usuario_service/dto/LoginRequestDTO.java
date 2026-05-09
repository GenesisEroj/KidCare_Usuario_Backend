package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO de entrada para el endpoint POST /api/auth/login. */
@Data
public class LoginRequestDTO {

    // Correo electrónico del usuario
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    // Contraseña del usuario
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}