package com.kidcare.usuario_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// Clase utilitaria que genera y valida tokens JWT
@Component
public class JwtUtil {

    // Clave secreta definida en application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de expiración del token en milisegundos (24 horas)
    @Value("${jwt.expiration}")
    private Long expiration;

    // Genera la clave de firma a partir del secret
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un token JWT con el email y rol del usuario
    public String generateToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el email del token JWT
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extrae el rol del token JWT
    public String getRolFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }

    // Valida si el token es válido y no está expirado
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}