package ar.edu.unlu.spgda.services;

import java.util.List;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ar.edu.unlu.spgda.dtos.GradesEmailDto;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.override:}")
    private String mailOverride;

    /**
     * Envía los emails de calificaciones de forma asíncrona.
     * Recibe DTOs (datos planos) para evitar LazyInitializationException fuera del hilo JPA.
     *
     * @param emailDtos Lista de DTOs con los datos necesarios para cada email.
     * @param eventId   ID del evento, solo para logging.
     */
    @Async
    public void sendGradesEmails(List<GradesEmailDto> emailDtos, long eventId) {
        logger.info("Iniciando envío asíncrono de calificaciones para el evento ID: {}", eventId);

        int sentCount = 0;
        int errorCount = 0;

        for (GradesEmailDto dto : emailDtos) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

                String content;
                if ("AUSENTE".equals(dto.getGrade())) {
                    content = String.format(
                        "Hola %s, te informamos que figuras como AUSENTE en el evento %s de la cursada %s.",
                        dto.getStudentName(),
                        dto.getEventName(),
                        dto.getCourseInfo()
                    );
                } else {
                    content = String.format(
                        "Hola %s, tu calificación para el evento %s de la cursada %s es: %s",
                        dto.getStudentName(),
                        dto.getEventName(),
                        dto.getCourseInfo(),
                        dto.getGrade()
                    );
                }

                String destinatarioFinal = dto.getStudentEmail();
                String asuntoFinal = "Calificación Registrada - " + dto.getEventName();
                // --- LÓGICA DE REDIRECCIÓN Y EDICIÓN DEL MENSAJE ---
                // Si la variable tiene texto, se activa el modo prueba
                if (mailOverride != null && !mailOverride.trim().isEmpty()) {
                    destinatarioFinal = mailOverride.trim(); 
                    asuntoFinal = "[SIMULACIÓN ENVÍO] " + asuntoFinal;
                    content = "=== CORREO DE PRUEBA (SIMULACIÓN) ===\n" +
                            "Este correo iba dirigido originalmente a: " + dto.getStudentEmail() + "\n" +
                            "=========================================\n\n" +
                            content;
                }
                helper.setTo(destinatarioFinal);
                helper.setSubject(asuntoFinal);
                helper.setText(content);

                mailSender.send(message);
                sentCount++;
                logger.debug("Email enviado a: {}", destinatarioFinal);

                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("Error al enviar email a {} para el evento {}", dto.getStudentEmail(), eventId, e);
                errorCount++;
            }
        }

        logger.info("Envío de correos finalizado para el evento {}. Enviados: {}, Errores: {}", eventId, sentCount, errorCount);
    }
}
