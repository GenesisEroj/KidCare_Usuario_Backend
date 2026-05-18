package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.dto.MenorRequestDTO;
import com.kidcare.usuario_service.dto.MenorResponseDTO;
import com.kidcare.usuario_service.model.Menor;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.model.UsuarioMenor;
import com.kidcare.usuario_service.model.UsuarioMenorId;
import com.kidcare.usuario_service.repository.MenorRepository;
import com.kidcare.usuario_service.repository.UsuarioMenorRepository;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para la gestión de perfiles de menores.
 *
 * <p>Administra la relación N:M entre usuarios y menores a través de la tabla
 * pivot {@code USUARIO_MENOR} ({@link UsuarioMenor}). Un tutor puede tener
 * varios menores y un menor puede ser accedido por varios usuarios (tutor + delegados).
 *
 * <p>Todas las operaciones de escritura validan que el usuario autenticado sea
 * propietario del menor antes de proceder.
 */
@Service
public class MenorService {

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private UsuarioMenorRepository usuarioMenorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea un perfil de menor y lo vincula al tutor autenticado.
     *
     * @param dto datos del menor (nombre, fecha de nacimiento, sexo)
     * @param emailTutor email del tutor autenticado
     * @return DTO con los datos del menor creado
     */
    public MenorResponseDTO crearMenor(MenorRequestDTO dto, String emailTutor) {

        // Busca el tutor por email
        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        // Crea el perfil del menor
        Menor menor = new Menor();
        menor.setNombre(dto.getNombre());
        menor.setFechaNacimiento(dto.getFechaNacimiento());
        menor.setSexo(dto.getSexo());
        menor.setEmoji(dto.getEmoji());
        menorRepository.save(menor);

        // Vincula el menor al tutor en la tabla pivot
        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(tutor.getIdUsuario());
        pivotId.setIdMenor(menor.getIdMenor());

        UsuarioMenor usuarioMenor = new UsuarioMenor();
        usuarioMenor.setId(pivotId);
        usuarioMenor.setUsuario(tutor);
        usuarioMenor.setMenor(menor);
        usuarioMenorRepository.save(usuarioMenor);

        return mapToDTO(menor);
    }

    /**
     * Retorna todos los menores vinculados al usuario autenticado.
     * Funciona para TUTOR (sus propios menores) y DELEGADO (menores con acceso asignado).
     *
     * @param emailTutor email del usuario autenticado
     * @return lista de menores vinculados al usuario
     */
    public List<MenorResponseDTO> obtenerMenoresPorTutor(String emailTutor) {

        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        Integer tutorId = tutor.getIdUsuario();
        return usuarioMenorRepository.findByIdIdUsuario(tutorId)
                .stream()
                .map(um -> {
                    MenorResponseDTO dto = mapToDTO(um.getMenor());
                    // Es delegado si hay otro usuario con rol TUTOR/ADMIN vinculado al mismo menor
                    boolean esDelegado = usuarioMenorRepository
                            .findByIdIdMenorAndIdIdUsuarioNot(um.getMenor().getIdMenor(), tutorId)
                            .stream()
                            .anyMatch(otro -> {
                                Usuario otroUsuario = usuarioRepository.findById(otro.getId().getIdUsuario()).orElse(null);
                                return otroUsuario != null &&
                                       (otroUsuario.getRol().getNombre().equalsIgnoreCase("TUTOR") ||
                                        otroUsuario.getRol().getNombre().equalsIgnoreCase("ADMIN"));
                            });
                    dto.setEsDelegado(esDelegado);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Edita el perfil de un menor verificando que el tutor sea su propietario.
     *
     * @param idMenor identificador del menor a editar
     * @param dto nuevos datos del menor
     * @param emailTutor email del tutor autenticado
     * @return DTO con los datos actualizados del menor
     * @throws RuntimeException si el tutor no tiene acceso al menor
     */
    public MenorResponseDTO editarMenor(Integer idMenor, MenorRequestDTO dto, String emailTutor) {

        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        // Verifica que el menor pertenezca al tutor
        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(tutor.getIdUsuario());
        pivotId.setIdMenor(idMenor);

        if (!usuarioMenorRepository.existsById(pivotId)) {
            throw new RuntimeException("No tienes acceso a este menor");
        }

        Menor menor = menorRepository.findById(idMenor)
                .orElseThrow(() -> new RuntimeException("Menor no encontrado"));

        menor.setNombre(dto.getNombre());
        menor.setFechaNacimiento(dto.getFechaNacimiento());
        menor.setSexo(dto.getSexo());
        menor.setEmoji(dto.getEmoji());
        menorRepository.save(menor);

        return mapToDTO(menor);
    }

    /**
     * Elimina el perfil de un menor y su vínculo con el tutor.
     *
     * @param idMenor identificador del menor a eliminar
     * @param emailTutor email del tutor autenticado
     * @throws RuntimeException si el tutor no tiene acceso al menor
     */
    public void eliminarMenor(Integer idMenor, String emailTutor) {

        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        // Verifica que el menor pertenezca al tutor
        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(tutor.getIdUsuario());
        pivotId.setIdMenor(idMenor);

        if (!usuarioMenorRepository.existsById(pivotId)) {
            throw new RuntimeException("No tienes acceso a este menor");
        }

        // Elimina el vínculo y luego el menor
        usuarioMenorRepository.deleteById(pivotId);
        menorRepository.deleteById(idMenor);
    }

    public MenorResponseDTO obtenerMenorPorId(Integer idMenor, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(usuario.getIdUsuario());
        pivotId.setIdMenor(idMenor);

        if (!usuarioMenorRepository.existsById(pivotId)) {
            throw new RuntimeException("No tienes acceso a este menor");
        }

        Menor menor = menorRepository.findById(idMenor)
                .orElseThrow(() -> new RuntimeException("Menor no encontrado"));

        return mapToDTO(menor);
    }

    public void vincularTutorAMenorExistente(Integer idMenor, String emailTutor) {
        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        Menor menor = menorRepository.findById(idMenor)
                .orElseThrow(() -> new RuntimeException("Menor no encontrado en el sistema"));

        UsuarioMenorId pivotId = new UsuarioMenorId();
        pivotId.setIdUsuario(tutor.getIdUsuario());
        pivotId.setIdMenor(menor.getIdMenor());

        if (usuarioMenorRepository.existsById(pivotId)) {
            throw new RuntimeException("Ya tienes acceso a este menor");
        }

        UsuarioMenor usuarioMenor = new UsuarioMenor();
        usuarioMenor.setId(pivotId);
        usuarioMenor.setUsuario(tutor);
        usuarioMenor.setMenor(menor);
        usuarioMenorRepository.save(usuarioMenor);
    }

    // Convierte una entidad Menor a MenorResponseDTO
    private MenorResponseDTO mapToDTO(Menor menor) {
        MenorResponseDTO dto = new MenorResponseDTO();
        dto.setIdMenor(menor.getIdMenor());
        dto.setNombre(menor.getNombre());
        dto.setFechaNacimiento(menor.getFechaNacimiento());
        dto.setSexo(menor.getSexo());
        dto.setEmoji(menor.getEmoji());
        return dto;
    }
}