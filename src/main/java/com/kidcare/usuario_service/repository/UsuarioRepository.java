package com.kidcare.usuario_service.repository;

import com.kidcare.usuario_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Repositorio que maneja el acceso a datos de la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Busca un usuario por su correo electrónico
    Optional<Usuario> findByEmail(String email);

    // Verifica si ya existe un usuario con ese correo
    Boolean existsByEmail(String email);

    // Busca un usuario por su token de recuperación de contraseña
    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);
}