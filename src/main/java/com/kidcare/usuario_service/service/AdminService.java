package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.dto.AdminUsuarioResponseDTO;
import com.kidcare.usuario_service.dto.AuditoriaResponseDTO;
import com.kidcare.usuario_service.model.Auditoria;
import com.kidcare.usuario_service.model.Rol;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.repository.AuditoriaRepository;
import com.kidcare.usuario_service.repository.RolRepository;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public List<AdminUsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
    }

    public void habilitarCuenta(Integer idUsuario, Integer idAdmin) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getActivo()) {
            throw new RuntimeException("La cuenta ya está habilitada");
        }

        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        auditoriaService.registrarAccion(idAdmin, "HABILITAR", "USUARIO", idUsuario);
    }

    public void deshabilitarCuenta(Integer idUsuario, Integer idAdmin) {
        if (idUsuario.equals(idAdmin)) {
            throw new RuntimeException("No puedes deshabilitar tu propia cuenta");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getActivo()) {
            throw new RuntimeException("La cuenta ya está deshabilitada");
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        auditoriaService.registrarAccion(idAdmin, "DESHABILITAR", "USUARIO", idUsuario);
    }

    public void asignarRol(Integer idUsuario, Integer idRol, Integer idAdmin) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRol(rol);
        usuarioRepository.save(usuario);
        auditoriaService.registrarAccion(idAdmin, "MODIFICAR_ROL", "USUARIO", idUsuario);
    }

    public List<AuditoriaResponseDTO> consultarAuditoria(String cambio, String entidad,
            LocalDate desde, LocalDate hasta) {
        List<Auditoria> registros;

        if (desde != null && hasta != null) {
            registros = auditoriaRepository.findByFechaBetween(desde, hasta);
        } else if (cambio != null && !cambio.isBlank()) {
            registros = auditoriaRepository.findByCambioContainingIgnoreCase(cambio);
        } else if (entidad != null && !entidad.isBlank()) {
            registros = auditoriaRepository.findByEntidad(entidad);
        } else {
            registros = auditoriaRepository.findAll();
        }

        return registros.stream()
                .map(this::mapToAuditoriaDTO)
                .collect(Collectors.toList());
    }

    private AdminUsuarioResponseDTO mapToAdminDTO(Usuario usuario) {
        AdminUsuarioResponseDTO dto = new AdminUsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().getNombre());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
    }

    private AuditoriaResponseDTO mapToAuditoriaDTO(Auditoria auditoria) {
        AuditoriaResponseDTO dto = new AuditoriaResponseDTO();
        dto.setIdAuditoria(auditoria.getIdAuditoria());
        dto.setEmailAdmin(auditoria.getUsuario().getEmail());
        dto.setCambio(auditoria.getCambio());
        dto.setEntidad(auditoria.getEntidad());
        dto.setIdEntidad(auditoria.getIdEntidad());
        dto.setFecha(auditoria.getFecha());
        return dto;
    }
}
