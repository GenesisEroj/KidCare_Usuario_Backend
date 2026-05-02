package com.kidcare.usuario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

// Clase que representa la clave primaria compuesta de UsuarioMenor
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