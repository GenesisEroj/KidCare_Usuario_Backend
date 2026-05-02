package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.UsuarioMenor;
import com.kidcare.usuario_service.model.UsuarioMenorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositorio que maneja el acceso a datos de la entidad UsuarioMenor
@Repository
public interface UsuarioMenorRepository extends JpaRepository<UsuarioMenor, UsuarioMenorId> {

    // Obtiene todos los menores vinculados a un usuario
    List<UsuarioMenor> findByIdIdUsuario(Integer idUsuario);

    // Obtiene todos los tutores vinculados a un menor
    List<UsuarioMenor> findByIdIdMenor(Integer idMenor);
}