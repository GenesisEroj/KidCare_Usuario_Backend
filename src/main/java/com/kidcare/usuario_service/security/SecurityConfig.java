package com.kidcare.usuario_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Configuración de seguridad del microservicio
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    // Define las reglas de acceso a los endpoints
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva CSRF porque usamos JWT
                .csrf(csrf -> csrf.disable())
                // Configura las rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas: registro y login
                        .requestMatchers("/api/auth/**").permitAll()
                        // Rutas de recuperación de contraseña
                        .requestMatchers("/api/password/**").permitAll()
                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated())
                // Sin estado de sesión, usamos JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agrega el filtro JWT antes del filtro de autenticación
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para encriptar contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}