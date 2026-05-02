package com.kidcare.usuario_service.controller;

import com.kidcare.usuario_service.dto.MenorRequestDTO;
import com.kidcare.usuario_service.dto.MenorResponseDTO;
import com.kidcare.usuario_service.service.MenorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador que expone los endpoints de gestión de menores
@RestController
@RequestMapping("/api/menores")
public class MenorController {

    @Autowired
    private MenorService menorService;

    // POST /api/menores — crea un perfil de menor vinculado al tutor autenticado
    @PostMapping
    public ResponseEntity<MenorResponseDTO> crear(@Valid @RequestBody MenorRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(menorService.crearMenor(dto, authentication.getName()));
    }

    // GET /api/menores — retorna todos los menores del tutor autenticado
    @GetMapping
    public ResponseEntity<List<MenorResponseDTO>> listar(Authentication authentication) {
        return ResponseEntity.ok(menorService.obtenerMenoresPorTutor(authentication.getName()));
    }

    // PUT /api/menores/{id} — edita el perfil de un menor del tutor autenticado
    @PutMapping("/{id}")
    public ResponseEntity<MenorResponseDTO> editar(@PathVariable Integer id,
            @Valid @RequestBody MenorRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(menorService.editarMenor(id, dto, authentication.getName()));
    }

    // DELETE /api/menores/{id} — elimina el perfil de un menor del tutor
    // autenticado
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id,
            Authentication authentication) {
        menorService.eliminarMenor(id, authentication.getName());
        return ResponseEntity.ok("Menor eliminado correctamente");
    }
}