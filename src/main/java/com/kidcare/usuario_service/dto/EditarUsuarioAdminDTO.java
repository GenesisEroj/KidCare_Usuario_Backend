package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditarUsuarioAdminDTO {

    @NotBlank
    private String nombreCompleto;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Integer idRol;
}
