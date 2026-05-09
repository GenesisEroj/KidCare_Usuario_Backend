package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa un rol del sistema.
 *
 * <p>Los tres roles disponibles (ADMIN, TUTOR, DELEGADO) se inicializan mediante
 * {@code INSERT IGNORE} en {@code data.sql} al arrancar la aplicación.
 * Spring Security los prefija con {@code ROLE_} al evaluarlos con {@code hasRole()}.
 */
@Data
@Entity
@Table(name = "ROL")
public class Rol {

    // Identificador único del rol, se genera automáticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    // Nombre del rol: ADMIN, TUTOR o DELEGADO
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Descripción opcional del rol
    @Column(name = "descripcion")
    private String descripcion;
}