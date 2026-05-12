package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvitacionRequestDTO {

    @NotBlank(message = "El email del delegado es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String emailDelegado;

    @NotNull(message = "El id del menor es obligatorio")
    private Integer idMenor;

    private String duracion;
}
