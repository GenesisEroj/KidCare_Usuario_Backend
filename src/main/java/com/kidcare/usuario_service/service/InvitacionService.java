package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.dto.CompletarRegistroDTO;
import com.kidcare.usuario_service.dto.InvitacionRequestDTO;
import com.kidcare.usuario_service.model.*;
import com.kidcare.usuario_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InvitacionService {

    @Autowired
    private InvitacionRepository invitacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioMenorRepository usuarioMenorRepository;

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void enviarInvitacion(String emailTutor, InvitacionRequestDTO dto) {
        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        if (usuarioRepository.existsByEmail(dto.getEmailDelegado())) {
            throw new RuntimeException("El correo ya está registrado. Si el usuario existe, usa vincular delegado.");
        }

        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(tutor.getIdUsuario());
        pivotId.setIdMenor(dto.getIdMenor());
        if (!usuarioMenorRepository.existsById(pivotId)) {
            throw new RuntimeException("No tienes acceso a este menor");
        }

        invitacionRepository.findByEmailInvitadoAndIdMenorAndUtilizadoFalse(
                dto.getEmailDelegado(), dto.getIdMenor())
                .ifPresent(inv -> {
                    if (inv.getFechaExpiracion().isAfter(LocalDateTime.now())) {
                        throw new RuntimeException("Ya existe una invitación activa para este delegado");
                    }
                });

        String token = UUID.randomUUID().toString();
        Invitacion invitacion = new Invitacion();
        invitacion.setEmailInvitado(dto.getEmailDelegado());
        invitacion.setIdMenor(dto.getIdMenor());
        invitacion.setIdTutor(tutor.getIdUsuario());
        invitacion.setToken(token);
        invitacion.setFechaExpiracion(LocalDateTime.now().plusHours(48));
        invitacion.setUtilizado(false);
        invitacionRepository.save(invitacion);

        emailService.enviarInvitacionDelegado(dto.getEmailDelegado(), token, dto.getIdMenor());
    }

    @Transactional
    public void completarRegistro(CompletarRegistroDTO dto) {
        Invitacion invitacion = invitacionRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Token de invitación inválido"));

        if (invitacion.getUtilizado()) {
            throw new RuntimeException("Esta invitación ya fue utilizada");
        }

        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La invitación ha expirado. Solicita una nueva al tutor.");
        }

        validarPassword(dto.getPassword());

        Rol rolDelegado = rolRepository.findByNombre("DELEGADO")
                .orElseThrow(() -> new RuntimeException("Rol DELEGADO no encontrado"));

        Usuario delegado = new Usuario();
        delegado.setNombreCompleto(dto.getNombreCompleto());
        delegado.setEmail(invitacion.getEmailInvitado());
        delegado.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        delegado.setRol(rolDelegado);
        delegado.setActivo(true);
        delegado.setFechaCreacion(LocalDate.now());
        usuarioRepository.save(delegado);

        Menor menor = menorRepository.findById(invitacion.getIdMenor())
                .orElseThrow(() -> new RuntimeException("Menor no encontrado"));

        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(delegado.getIdUsuario());
        pivotId.setIdMenor(menor.getIdMenor());

        UsuarioMenor usuarioMenor = new UsuarioMenor();
        usuarioMenor.setId(pivotId);
        usuarioMenor.setUsuario(delegado);
        usuarioMenor.setMenor(menor);
        usuarioMenorRepository.save(usuarioMenor);

        invitacion.setUtilizado(true);
        invitacionRepository.save(invitacion);
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            throw new RuntimeException("La contraseña debe contener al menos una letra mayúscula");
        }
        if (password.chars().allMatch(Character::isLetterOrDigit)) {
            throw new RuntimeException("La contraseña debe contener al menos un símbolo especial");
        }
    }
}
