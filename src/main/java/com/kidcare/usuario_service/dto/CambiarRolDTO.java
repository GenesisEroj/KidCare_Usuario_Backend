package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarRolDTO {

    @NotNull(message = "El id del rol es obligatorio")
    private Integer idRol;
}
