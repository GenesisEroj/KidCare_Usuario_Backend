package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Repositorio JPA para la entidad {@link Permiso}. */
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    Optional<Permiso> findByNombrePermiso(String nombrePermiso);
}
