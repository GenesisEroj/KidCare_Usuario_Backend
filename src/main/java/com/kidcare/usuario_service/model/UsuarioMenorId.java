package com.kidcare.usuario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

/**
 * Clave primaria compuesta de la tabla pivot {@code USUARIO_MENOR}.
 *
 * <p>Implementa {@link Serializable} como requiere JPA para claves embebidas.
 */
@Data
@Embeddable
public class UsuarioMenorId implements Serializable {

    // Referencia al id del usuario
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Referencia al id del menor
    @Column(name = "id_menor")
    private Integer idMenor;
}