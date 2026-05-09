package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;

// Entidad que representa las acciones permitidas en el sistema
@Data
@Entity
@Table(name = "PERMISO")
public class Permiso {

    // Identificador único del permiso
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Integer idPermiso;

    // Nombre descriptivo de la acción permitida
    @Column(name = "nombre_permiso", nullable = false, unique = true)
    private String nombrePermiso;

    // Descripción opcional de la acción
    @Column(name = "descripcion")
    private String descripcion;
}