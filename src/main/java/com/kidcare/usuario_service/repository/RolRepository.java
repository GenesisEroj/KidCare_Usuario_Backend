package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Repositorio que maneja el acceso a datos de la entidad Rol
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Busca un rol por su nombre (ADMIN, TUTOR, DELEGADO)
    Optional<Rol> findByNombre(String nombre);
}