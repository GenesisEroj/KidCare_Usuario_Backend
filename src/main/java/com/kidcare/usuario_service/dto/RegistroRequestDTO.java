package com.kidcare.usuario_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada para el endpoint POST /api/auth/registro.
 *
 * <p>El registro público solo permite crear cuentas TUTOR.
 * El rol DELEGADO es asignado por un tutor mediante /api/delegados/vincular.
 * {@code aceptaTerminos} es obligatorio por la Ley 19.628 de protección de datos.
 */
@Data
public class RegistroRequestDTO {

    // Nombre completo del usuario (nombre + apellidos combinados)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    // Correo electrónico del usuario
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    // Contraseña: mínimo 8 caracteres, una mayúscula y un símbolo especial
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // Teléfono de contacto (opcional)
    private String telefono;

    // Aceptación de términos y condiciones (obligatorio por Ley 19.628)
    private Boolean aceptaTerminos;
}
