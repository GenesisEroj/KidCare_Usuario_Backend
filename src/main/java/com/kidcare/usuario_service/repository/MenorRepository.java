package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Menor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio que maneja el acceso a datos de la entidad Menor
@Repository
public interface MenorRepository extends JpaRepository<Menor, Integer> {
}
