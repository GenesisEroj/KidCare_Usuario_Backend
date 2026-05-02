package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;

// Entidad pivot que resuelve la relación N:M entre Rol y Permiso
@Data
@Entity
@Table(name = "ROL_PERMISO")
public class RolPermiso {

    // Clave primaria compuesta por id_rol e id_permiso
    @EmbeddedId
    private RolPermisoId id;

    // Relación con la entidad Rol
    @ManyToOne
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

    // Relación con la entidad Permiso
    @ManyToOne
    @MapsId("idPermiso")
    @JoinColumn(name = "id_permiso")
    private Permiso permiso;
}