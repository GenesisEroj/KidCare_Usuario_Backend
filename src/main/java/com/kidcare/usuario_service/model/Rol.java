package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;

// Entidad que representa los roles del sistema (ADMIN, TUTOR, DELEGADO)
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