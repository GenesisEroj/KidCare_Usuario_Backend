package com.kidcare.usuario_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${mail.from:kidcare@kidcare.com}")
    private String remitente;

    @Value("${mail.dev-mode:true}")
    private boolean devMode;

    private final RestTemplate restTemplate = new RestTemplate();

    private void enviar(String destinatario, String asunto, String cuerpo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("sender",  Map.of("name", "KidCare", "email", remitente));
        body.put("to",      List.of(Map.of("email", destinatario)));
        body.put("subject", asunto);
        body.put("textContent", cuerpo);

        restTemplate.postForEntity(
            "https://api.brevo.com/v3/smtp/email",
            new HttpEntity<>(body, headers),
            Map.class
        );
    }

    public void enviarCorreoRecuperacion(String destinatario, String token) {
        if (devMode) {
            System.out.println("\n========================================");
            System.out.println("  [DEV] TOKEN DE RECUPERACIÓN");
            System.out.println("  Destinatario : " + destinatario);
            System.out.println("  Token        : " + token);
            System.out.println("========================================\n");
            return;
        }
        enviar(destinatario,
            "KidCare — Recuperación de contraseña",
            "Hola,\n\n" +
            "Tu código de recuperación es:\n\n    " + token + "\n\n" +
            "Ingresa este código en la app junto con tu nueva contraseña.\n" +
            "El código es válido por 24 horas.\n\n" +
            "Si no solicitaste este cambio, ignora este correo.\n\n" +
            "Equipo KidCare"
        );
    }

    public void enviarInvitacionDelegado(String destinatario, String token, Integer idMenor) {
        if (devMode) {
            System.out.println("\n========================================");
            System.out.println("  [DEV] INVITACIÓN A DELEGADO");
            System.out.println("  Destinatario : " + destinatario);
            System.out.println("  Token        : " + token);
            System.out.println("  Menor ID     : " + idMenor);
            System.out.println("========================================\n");
            return;
        }
        enviar(destinatario,
            "KidCare — Has sido invitado como delegado",
            "Hola,\n\n" +
            "Has sido invitado a acceder al perfil de salud de un menor en KidCare.\n\n" +
            "Para completar tu registro, usa el siguiente token en la aplicación:\n\n" +
            "    " + token + "\n\n" +
            "Este token es válido por 48 horas.\n\n" +
            "Si no esperabas esta invitación, ignora este correo.\n\n" +
            "Equipo KidCare"
        );
    }
}
