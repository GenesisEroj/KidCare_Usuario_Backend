package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositorio que maneja el acceso a datos de la entidad Auditoria
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    // Obtiene todos los registros de auditoría de un usuario administrador
    List<Auditoria> findByUsuarioIdUsuario(Integer idUsuario);
}