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

// Servicio que maneja la gestión de perfiles de menores
@Service
public class MenorService {

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private UsuarioMenorRepository usuarioMenorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crea un perfil de menor y lo vincula al tutor autenticado
    public MenorResponseDTO crearMenor(MenorRequestDTO dto, String emailTutor) {

        // Busca el tutor por email
        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        // Crea el perfil del menor
        Menor menor = new Menor();
        menor.setNombre(dto.getNombre());
        menor.setFechaNacimiento(dto.getFechaNacimiento());
        menor.setSexo(dto.getSexo());
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

    // Retorna todos los menores vinculados al tutor autenticado
    public List<MenorResponseDTO> obtenerMenoresPorTutor(String emailTutor) {

        Usuario tutor = usuarioRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        return usuarioMenorRepository.findByIdIdUsuario(tutor.getIdUsuario())
                .stream()
                .map(um -> mapToDTO(um.getMenor()))
                .collect(Collectors.toList());
    }

    // Edita el perfil de un menor verificando que pertenezca al tutor
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
        menorRepository.save(menor);

        return mapToDTO(menor);
    }

    // Elimina el perfil de un menor y su vínculo con el tutor
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

    // Convierte una entidad Menor a MenorResponseDTO
    private MenorResponseDTO mapToDTO(Menor menor) {
        MenorResponseDTO dto = new MenorResponseDTO();
        dto.setIdMenor(menor.getIdMenor());
        dto.setNombre(menor.getNombre());
        dto.setFechaNacimiento(menor.getFechaNacimiento());
        dto.setSexo(menor.getSexo());
        return dto;
    }
}