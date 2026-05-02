package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO que recibe los datos para iniciar sesión
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