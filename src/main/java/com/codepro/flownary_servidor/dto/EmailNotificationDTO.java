package com.codepro.flownary_servidor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para las notificaciones por correo electrónico.
 * Contiene la información necesaria para enviar una notificación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationDTO {
    
    private String to;
    private String subject;
    private String content;
    private boolean isHtml;
    
    public EmailNotificationDTO(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.isHtml = true; // Por defecto usamos HTML
    }
}
