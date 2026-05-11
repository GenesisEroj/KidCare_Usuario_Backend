package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findByUsuarioIdUsuario(Integer idUsuario);
    List<Auditoria> findByCambioContainingIgnoreCase(String cambio);
    List<Auditoria> findByEntidad(String entidad);
    List<Auditoria> findByFechaBetween(LocalDate desde, LocalDate hasta);
}