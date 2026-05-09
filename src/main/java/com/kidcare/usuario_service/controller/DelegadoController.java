package com.kidcare.usuario_service.controller;

import com.kidcare.usuario_service.dto.VincularDelegadoRequestDTO;
import com.kidcare.usuario_service.service.DelegadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la vinculación de apoderados a menores.
 *
 * <p>Solo accesible para usuarios con rol TUTOR o ADMIN según las reglas
 * definidas en {@link com.kidcare.usuario_service.security.SecurityConfig}.
 * El tutor debe ser propietario del menor para poder vincular un apoderado.
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>POST /api/delegados/vincular — vincula un DELEGADO a un menor del tutor autenticado</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/delegados")
public class DelegadoController {

    @Autowired
    private DelegadoService delegadoService;

    /**
     * Vincula un apoderado (DELEGADO) a un menor del tutor autenticado.
     *
     * @param dto email del apoderado e ID del menor
     * @param authentication contexto de seguridad; extrae el email del tutor
     * @return 200 con mensaje de confirmación — 400 si el tutor no posee el menor
     *         o el email no corresponde a un DELEGADO registrado
     */
    @PostMapping("/vincular")
    public ResponseEntity<String> vincular(@RequestBody VincularDelegadoRequestDTO dto,
            Authentication authentication) {
        delegadoService.vincularDelegado(authentication.getName(), dto.getEmailDelegado(), dto.getIdMenor());
        return ResponseEntity.ok("Apoderado vinculado correctamente");
    }
}
