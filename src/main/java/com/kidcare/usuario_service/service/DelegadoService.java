package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.model.Menor;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.model.UsuarioMenor;
import com.kidcare.usuario_service.model.UsuarioMenorId;
import com.kidcare.usuario_service.repository.MenorRepository;
import com.kidcare.usuario_service.repository.UsuarioMenorRepository;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio de negocio para la vinculación de apoderados a menores.
 *
 * <p>Valida tres condiciones antes de crear el vínculo:
 * <ol>
 *   <li>El tutor autenticado posee el menor indicado.</li>
 *   <li>El usuario con el email proporcionado existe y tiene rol DELEGADO.</li>
 *   <li>El apoderado no está ya vinculado a ese menor.</li>
 * </ol>
 */
@Service
public class DelegadoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMenorRepository usuarioMenorRepository;

    @Autowired
    private MenorRepository menorRepository;

    /**
     * Vincula un apoderado (DELEGADO) a un menor del tutor.
     *
     * @param emailTutor    email del tutor autenticado (propietario del menor)
     * @param emailDelegado email del usuario DELEGADO a vincular
     * @param idMenor       identificador del menor al que se da acceso
     * @throws RuntimeException si alguna de las validaciones falla
     */
    public void vincularDelegado(String emailTutor, String emailDelegado, Integer idMenor) {

        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        // Verifica que el tutor tenga acceso al menor
        UsuarioMenorId tutorMenorId = new UsuarioMenorId();
        tutorMenorId.setIdUsuario(tutor.getIdUsuario());
        tutorMenorId.setIdMenor(idMenor);
        if (!usuarioMenorRepository.existsById(tutorMenorId)) {
            throw new RuntimeException("No tienes acceso a este menor");
        }

        Usuario delegado = usuarioRepository.findByEmail(emailDelegado)
                .orElseThrow(() -> new RuntimeException("El apoderado no está registrado en el sistema"));

        if (!delegado.getRol().getNombre().equalsIgnoreCase("DELEGADO")) {
            throw new RuntimeException("El usuario no tiene rol de apoderado");
        }

        // Verifica que no esté vinculado ya
        UsuarioMenorId delegadoMenorId = new UsuarioMenorId();
        delegadoMenorId.setIdUsuario(delegado.getIdUsuario());
        delegadoMenorId.setIdMenor(idMenor);
        if (usuarioMenorRepository.existsById(delegadoMenorId)) {
            throw new RuntimeException("El apoderado ya tiene acceso a este menor");
        }

        Menor menor = menorRepository.findById(idMenor)
                .orElseThrow(() -> new RuntimeException("Menor no encontrado"));

        UsuarioMenor link = new UsuarioMenor();
        link.setId(delegadoMenorId);
        link.setUsuario(delegado);
        link.setMenor(menor);
        usuarioMenorRepository.save(link);
    }
}
