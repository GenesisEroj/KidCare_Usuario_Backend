package com.kidcare.usuario_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio responsable del envío de correos electrónicos del sistema KidCare.
 * Utiliza Gmail SMTP a través de Spring Boot Mail (JavaMailSender).
 *
 * <p>Configuración requerida en application.properties:
 * <ul>
 *   <li>spring.mail.username — correo Gmail del remitente</li>
 *   <li>spring.mail.password — contraseña de aplicación de Google</li>
 * </ul>
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    /**
     * Envía un correo de recuperación de contraseña al usuario.
     *
     * <p>El correo contiene el token UUID generado por {@link AuthService#solicitarRecuperacion},
     * que el usuario debe ingresar en la app para crear una nueva contraseña.
     *
     * @param destinatario correo electrónico del usuario que olvidó su contraseña
     * @param token        UUID de recuperación válido por 24 horas
     */
    public void enviarCorreoRecuperacion(String destinatario, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("KidCare — Recuperación de contraseña");
        mensaje.setText(
            "Hola,\n\n" +
            "Recibimos una solicitud para recuperar tu contraseña de KidCare.\n\n" +
            "Tu código de recuperación es:\n\n" +
            "    " + token + "\n\n" +
            "Ingresa este código en la app junto con tu nueva contraseña.\n" +
            "El código es válido por 24 horas.\n\n" +
            "Si no solicitaste este cambio, ignora este correo. " +
            "Tu contraseña actual permanece sin cambios.\n\n" +
            "Equipo KidCare\n" +
            "Bitácora de salud pediátrica"
        );
        mailSender.send(mensaje);
    }
}
