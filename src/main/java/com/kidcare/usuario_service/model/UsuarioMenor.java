package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

// Entidad pivot que resuelve la relación N:M entre Usuario y Menor
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