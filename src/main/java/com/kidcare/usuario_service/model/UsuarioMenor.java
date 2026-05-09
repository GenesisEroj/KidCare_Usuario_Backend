package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Entidad JPA de tabla pivot que resuelve la relación N:M entre {@link Usuario} y {@link Menor}.
 *
 * <p>Permite que un menor esté vinculado a su tutor principal y a uno o más apoderados,
 * y que un usuario (tutor o delegado) tenga acceso a múltiples menores.
 * La clave primaria compuesta está definida en {@link UsuarioMenorId}.
 */
@Data
@Entity
@Table(name = "USUARIO_MENOR")
public class UsuarioMenor {

    // Clave primaria compuesta por id_usuario e id_menor
    @EmbeddedId
    private UsuarioMenorId id;

    // Relación con la entidad Usuario
    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Relación con la entidad Menor
    @ManyToOne
    @MapsId("idMenor")
    @JoinColumn(name = "id_menor")
    private Menor menor;
}