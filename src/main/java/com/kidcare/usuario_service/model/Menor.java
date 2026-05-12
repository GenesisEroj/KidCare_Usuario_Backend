package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entidad JPA que representa el perfil de un menor en el sistema.
 *
 * <p>Un menor puede estar vinculado a múltiples usuarios (su tutor y uno o más
 * apoderados) a través de la tabla pivot {@code USUARIO_MENOR} ({@link UsuarioMenor}).
 */
@Data
@Entity
@Table(name = "MENOR")
public class Menor {

    // Identificador único del menor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menor")
    private Integer idMenor;

    // Nombre completo del menor
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Fecha de nacimiento del menor
    @Column(name = "fechaNacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Sexo del menor
    @Column(name = "sexo", nullable = false)
    private String sexo;

    // Emoji representativo del perfil del menor (opcional)
    @Column(name = "emoji")
    private String emoji;
}