package com.codepro.flownary_servidor.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Cliente SMTP genérico para envío de correos.
 */
@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo electrónico con formato HTML de forma asíncrona.
     * * @param to Destinatario
     * @param subject Asunto del correo
     * @param htmlContent Cuerpo del mensaje en formato HTML
     * @return true si se procesó con éxito, false en caso de error
     */
    @Async
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // El parámetro 'true' habilita modo multipart, "UTF-8" asegura codificación correcta de tildes
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que el texto es HTML

            mailSender.send(message);
            log.info("Correo enviado exitosamente mediante SMTP a: {}", to);
            return true;

        } catch (MessagingException e) {
            log.error("Excepción SMTP al enviar correo a {}: {}", to, e.getMessage());
            return false;
        }
    }
}