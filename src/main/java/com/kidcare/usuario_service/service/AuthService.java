package com.kidcare.usuario_service.service;

import com.kidcare.usuario_service.dto.*;
import com.kidcare.usuario_service.model.Rol;
import com.kidcare.usuario_service.model.Usuario;
import com.kidcare.usuario_service.repository.RolRepository;
import com.kidcare.usuario_service.repository.UsuarioRepository;
import com.kidcare.usuario_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Servicio de autenticación y gestión de cuentas de usuario.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Registro de nuevos usuarios (TUTOR o DELEGADO)</li>
 *   <li>Autenticación y generación de token JWT</li>
 *   <li>Solicitud y restablecimiento de contraseña via email</li>
 * </ul>
 */
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>El rol se determina por el campo {@code rolNombre} del DTO:
     * si vale "DELEGADO" se crea con ese rol, en cualquier otro caso se asigna TUTOR.
     * Al registrarse exitosamente se genera y retorna un token JWT listo para usar.
     *
     * @param dto datos del nuevo usuario (nombre, email, contraseña, términos, rolNombre)
     * @return token JWT + email + nombre del rol asignado
     * @throws RuntimeException si el email ya está registrado o el rol no existe en BD
     */
    public AuthResponseDTO registrar(RegistroRequestDTO dto) {

        // Verifica que el usuario aceptó los términos y condiciones
        if (dto.getAceptaTerminos() == null || !dto.getAceptaTerminos()) {
            throw new RuntimeException("Debe aceptar los términos y condiciones");
        }

        // Verifica que el correo no esté registrado
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Determina el rol: TUTOR por defecto, acepta DELEGADO
        String nombreRol = "DELEGADO".equalsIgnoreCase(dto.getRolNombre()) ? "DELEGADO" : "TUTOR";
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol " + nombreRol + " no encontrado"));

        // Crea el nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDate.now());

        usuarioRepository.save(usuario);

        // Genera y retorna el token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), rol.getNombre(), usuario.getIdUsuario());
        return new AuthResponseDTO(token, usuario.getEmail(), rol.getNombre());
    }

    /**
     * Autentica a un usuario con email y contraseña.
     *
     * <p>Verifica que la cuenta exista, esté activa y que la contraseña
     * coincida con el hash almacenado (BCrypt). Retorna un JWT válido por
     * el tiempo configurado en {@code jwt.expiration}.
     *
     * @param dto email y contraseña del usuario
     * @return token JWT + email + nombre del rol
     * @throws RuntimeException si las credenciales son incorrectas o la cuenta está desactivada
     */
    public AuthResponseDTO login(LoginRequestDTO dto) {

        // Busca el usuario por correo
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // Verifica que la cuenta esté activa
        if (!usuario.getActivo()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        // Verifica la contraseña
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // Genera y retorna el token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().getNombre(), usuario.getIdUsuario());
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getRol().getNombre());
    }

    /**
     * Genera un token de recuperación y envía el correo al usuario.
     *
     * <p>El token es un UUID aleatorio con vigencia de 24 horas.
     * Se envía al correo del usuario via Gmail SMTP a través de {@link EmailService}.
     *
     * @param dto correo electrónico del usuario que olvidó su contraseña
     * @throws RuntimeException si el correo no está registrado en el sistema
     */
    public void solicitarRecuperacion(RecuperarPasswordRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Correo no registrado en el sistema"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setFechaExpiracionToken(LocalDate.now().plusDays(1));
        usuarioRepository.save(usuario);

        emailService.enviarCorreoRecuperacion(usuario.getEmail(), token);
    }

    /**
     * Restablece la contraseña del usuario usando el token recibido por correo.
     *
     * <p>Valida que el token exista y no haya expirado antes de actualizar la contraseña.
     * Tras el restablecimiento, el token se invalida para evitar reutilización.
     *
     * @param dto token de recuperación y nueva contraseña
     * @throws RuntimeException si el token es inválido o ha expirado
     */
    public void restablecerPassword(NuevaPasswordRequestDTO dto) {

        // Busca el usuario por token de recuperación
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verifica que el token no haya expirado
        if (usuario.getFechaExpiracionToken().isBefore(LocalDate.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        // Actualiza la contraseña y limpia el token
        usuario.setPasswordHash(passwordEncoder.encode(dto.getNuevaPassword()));
        usuario.setTokenRecuperacion(null);
        usuario.setFechaExpiracionToken(null);

        usuarioRepository.save(usuario);
    }
}