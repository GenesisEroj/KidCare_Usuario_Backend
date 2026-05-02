package com.kidcare.usuario_service.controller;

import com.kidcare.usuario_service.dto.NuevaPasswordRequestDTO;
import com.kidcare.usuario_service.dto.RecuperarPasswordRequestDTO;
import com.kidcare.usuario_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controlador que expone los endpoints de recuperación de contraseña
@RestController
@RequestMapping("/api/password")
public class PasswordController {

    @Autowired
    private AuthService authService;

    // POST /api/password/recuperar — envía token de recuperación al correo
    @PostMapping("/recuperar")
    public ResponseEntity<String> recuperar(@Valid @RequestBody RecuperarPasswordRequestDTO dto) {
        authService.solicitarRecuperacion(dto);
        return ResponseEntity.ok("Token de recuperación enviado");
    }

    // POST /api/password/restablecer — restablece la contraseña con el token
    @PostMapping("/restablecer")
    public ResponseEntity<String> restablecer(@Valid @RequestBody NuevaPasswordRequestDTO dto) {
        authService.restablecerPassword(dto);
        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }
}