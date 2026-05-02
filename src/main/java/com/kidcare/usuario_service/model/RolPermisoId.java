package com.kidcare.usuario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

// Clase que representa la clave primaria compuesta de RolPermiso
@Data
@Embeddable
public class RolPermisoId implements Serializable {

    // Referencia al id del rol
    @Column(name = "id_rol")
    private Integer idRol;

    // Referencia al id del permiso
    @Column(name = "id_permiso")
    private Integer idPermiso;
}