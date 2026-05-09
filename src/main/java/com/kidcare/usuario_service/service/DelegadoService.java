package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.model.Menor;
import com.kidcare.usuario_service.model.Rol;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.model.UsuarioMenor;
import com.kidcare.usuario_service.model.UsuarioMenorId;
import com.kidcare.usuario_service.repository.MenorRepository;
import com.kidcare.usuario_service.repository.RolRepository;
import com.kidcare.usuario_service.repository.UsuarioMenorRepository;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio para la vinculación de apoderados a menores.
 *
 * <p>Valida dos condiciones antes de crear el vínculo:
 * <ol>
 *   <li>El tutor autenticado posee el menor indicado.</li>
 *   <li>El usuario destinatario existe y no está ya vinculado al menor.</li>
 * </ol>
 * Si el usuario destinatario tiene rol TUTOR se deja intacto (ya tiene permisos
 * equivalentes o superiores). Si tiene otro rol se le asigna DELEGADO
 * automáticamente para que pueda acceder al historial del menor.
 */
@Service
public class DelegadoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMenorRepository usuarioMenorRepository;

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private RolRepository rolRepository;

    /**
     * Vincula un usuario a un menor del tutor, asignándole acceso como apoderado.
     *
     * @param emailTutor    email del tutor autenticado (propietario del menor)
     * @param emailDelegado email del usuario a vincular
     * @param idMenor       identificador del menor al que se da acceso
     * @throws RuntimeException si alguna de las validaciones falla
     */
    @Transactional
    public void vincularDelegado(String emailTutor, String emailDelegado, Integer idMenor) {

        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        UsuarioMenorId tutorMenorId = new UsuarioMenorId();
        tutorMenorId.setIdUsuario(tutor.getIdUsuario());
        tutorMenorId.setIdMenor(idMenor);
        if (!usuarioMenorRepository.existsById(tutorMenorId)) {
            throw new RuntimeException("No tienes acceso a este menor");
        }

        Usuario delegado = usuarioRepository.findByEmail(emailDelegado)
                .orElseThrow(() -> new RuntimeException("El usuario no está registrado en el sistema"));

        // Si el usuario no es TUTOR ni ADMIN, asignarle rol DELEGADO automáticamente
        String rolActual = delegado.getRol().getNombre();
        if (!rolActual.equalsIgnoreCase("TUTOR") && !rolActual.equalsIgnoreCase("ADMIN")) {
            Rol rolDelegado = rolRepository.findByNombre("DELEGADO")
                    .orElseThrow(() -> new RuntimeException("Rol DELEGADO no encontrado"));
            delegado.setRol(rolDelegado);
            usuarioRepository.save(delegado);
        }

        UsuarioMenorId delegadoMenorId = new UsuarioMenorId();
        delegadoMenorId.setIdUsuario(delegado.getIdUsuario());
        delegadoMenorId.setIdMenor(idMenor);
        if (usuarioMenorRepository.existsById(delegadoMenorId)) {
            throw new RuntimeException("El usuario ya tiene acceso a este menor");
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
