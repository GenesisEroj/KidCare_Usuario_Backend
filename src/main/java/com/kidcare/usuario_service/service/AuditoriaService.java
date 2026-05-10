package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.model.Auditoria;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.repository.AuditoriaRepository;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void registrarAccion(Integer idAdmin, String cambio, String entidad, Integer idEntidad) {
        Usuario admin = usuarioRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(admin);
        auditoria.setCambio(cambio);
        auditoria.setEntidad(entidad);
        auditoria.setIdEntidad(idEntidad);
        auditoria.setFecha(LocalDate.now());

        auditoriaRepository.save(auditoria);
    }
}
