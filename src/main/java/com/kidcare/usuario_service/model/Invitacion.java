package com.kidcare.usuario_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "INVITACION")
public class Invitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitacion")
    private Integer idInvitacion;

    @Column(name = "email_invitado", nullable = false)
    private String emailInvitado;

    @Column(name = "id_menor", nullable = false)
    private Integer idMenor;

    @Column(name = "id_tutor", nullable = false)
    private Integer idTutor;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "utilizado", nullable = false)
    private Boolean utilizado = false;
}
