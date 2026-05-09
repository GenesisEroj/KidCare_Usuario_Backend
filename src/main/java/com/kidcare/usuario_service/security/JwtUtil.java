package com.kidcare.usuario_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad para generación y validación de tokens JWT.
 *
 * <p>Utiliza HMAC-SHA256 con la clave configurada en {@code jwt.secret}.
 * El tiempo de expiración se lee de {@code jwt.expiration} (en milisegundos).
 *
 * <p>Cada token contiene los claims:
 * <ul>
 *   <li>{@code sub} — email del usuario (subject)</li>
 *   <li>{@code rol} — nombre del rol (TUTOR, DELEGADO, ADMIN)</li>
 *   <li>{@code idUsuario} — ID numérico del usuario en MySQL</li>
 * </ul>
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT firmado con HMAC-SHA256.
     *
     * @param email     correo del usuario (usado como subject)
     * @param rol       nombre del rol (ej. "TUTOR")
     * @param idUsuario ID del usuario en la base de datos
     * @return token JWT compacto listo para enviar como header Bearer
     */
    public String generateToken(String email, String rol, Integer idUsuario) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .claim("idUsuario", idUsuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    /**
     * Extrae el email (subject) del token JWT.
     *
     * @param token token JWT compacto
     * @return email del usuario
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extrae el nombre del rol del token JWT.
     *
     * @param token token JWT compacto
     * @return nombre del rol (ej. "TUTOR", "DELEGADO")
     */
    public String getRolFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("rol", String.class);
    }

    /**
     * Valida la firma y la expiración del token JWT.
     *
     * @param token token JWT compacto
     * @return {@code true} si el token es válido y no ha expirado; {@code false} en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}