package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompletarRegistroDTO {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
