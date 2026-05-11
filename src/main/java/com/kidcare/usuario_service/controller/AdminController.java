package com.kidcare.usuario_service.controller;

import com.kidcare.usuario_service.dto.AdminUsuarioResponseDTO;
import com.kidcare.usuario_service.dto.AuditoriaResponseDTO;
import com.kidcare.usuario_service.dto.CambiarRolDTO;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import com.kidcare.usuario_service.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // CU015: Listar todos los usuarios del sistema
    @GetMapping("/usuarios")
    public ResponseEntity<List<AdminUsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    // CU016: Habilitar cuenta deshabilitada
    @PatchMapping("/usuarios/{id}/habilitar")
    public ResponseEntity<String> habilitarCuenta(@PathVariable Integer id,
            Authentication authentication) {
        adminService.habilitarCuenta(id, obtenerIdAdmin(authentication));
        return ResponseEntity.ok("Cuenta habilitada correctamente");
    }

    // CU017: Deshabilitar cuenta activa
    @PatchMapping("/usuarios/{id}/deshabilitar")
    public ResponseEntity<String> deshabilitarCuenta(@PathVariable Integer id,
            Authentication authentication) {
        adminService.deshabilitarCuenta(id, obtenerIdAdmin(authentication));
        return ResponseEntity.ok("Cuenta deshabilitada correctamente");
    }

    // CU018: Asignar nuevo rol a usuario
    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<String> asignarRol(@PathVariable Integer id,
            @Valid @RequestBody CambiarRolDTO dto,
            Authentication authentication) {
        adminService.asignarRol(id, dto.getIdRol(), obtenerIdAdmin(authentication));
        return ResponseEntity.ok("Rol actualizado correctamente");
    }

    // CU019: Consultar auditoría con filtros opcionales
    @GetMapping("/auditoria")
    public ResponseEntity<List<AuditoriaResponseDTO>> consultarAuditoria(
            @RequestParam(required = false) String cambio,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(adminService.consultarAuditoria(cambio, entidad, desde, hasta));
    }

    private Integer obtenerIdAdmin(Authentication authentication) {
        String email = authentication.getName();
        Usuario admin = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        return admin.getIdUsuario();
    }
}
