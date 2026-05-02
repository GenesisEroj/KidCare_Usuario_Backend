package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// Entidad que registra las acciones ejecutadas por el administrador
@Data
@Entity
@Table(name = "AUDITORIA")
public class Auditoria {

    // Identificador único del registro de auditoría
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    // Admin que ejecutó la acción
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Tipo de acción: Modificar, Desactivar, Habilitar, etc.
    @Column(name = "cambio", nullable = false)
    private String cambio;

    // Tabla afectada: USUARIO, ROL_PERMISO, etc.
    @Column(name = "entidad", nullable = false)
    private String entidad;

    // ID del registro afectado
    @Column(name = "id_entidad", nullable = false)
    private Integer idEntidad;

    // Fecha en que se ejecutó la acción
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
}