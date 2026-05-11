package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.dto.BienvenidaEmailDTO;
import com.codepro.flownary_servidor.dto.EquipoAsignadoEmailDTO;
import com.codepro.flownary_servidor.dto.EmailNotificationDTO;
import com.codepro.flownary_servidor.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Servicio de notificaciones por correo electrónico.
 * Centraliza la lógica de envío de correos para diferentes eventos del sistema.
 */
@Service
@Slf4j
public class NotificacionService {

    @Autowired
    private EmailService emailService;

    @Value("${app.email.from:noreply@flownary.com}")
    private String emailFrom;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Envía correo de bienvenida a un nuevo usuario.
     * 
     * @param bienvenidaDTO Información del usuario para el correo de bienvenida
     * @return true si el envío fue exitoso, false en caso contrario
     */
    public boolean enviarBienvenidaUsuario(BienvenidaEmailDTO bienvenidaDTO) {
        if (!emailEnabled) {
            log.info("Envío de correos deshabilitado. Omitiendo correo de bienvenida para: {}", bienvenidaDTO.getEmailUsuario());
            return true;
        }

        try {
            String subject = "¡Bienvenido(a) a " + bienvenidaDTO.getNombreApp() + "!";
            String content = generarPlantillaBienvenida(bienvenidaDTO);
            
            EmailNotificationDTO notification = new EmailNotificationDTO(
                bienvenidaDTO.getEmailUsuario(),
                subject,
                content,
                true
            );
            
            boolean enviado = emailService.sendHtmlEmail(notification.getTo(), notification.getSubject(), notification.getContent());
            
            if (enviado) {
                log.info("Correo de bienvenida enviado exitosamente a: {}", bienvenidaDTO.getEmailUsuario());
            } else {
                log.error("Error al enviar correo de bienvenida a: {}", bienvenidaDTO.getEmailUsuario());
            }
            
            return enviado;
            
        } catch (Exception e) {
            log.error("Error al procesar correo de bienvenida para {}: {}", bienvenidaDTO.getEmailUsuario(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Envía correo de notificación cuando un usuario es agregado a un equipo.
     * 
     * @param equipoAsignadoDTO Información de la asignación al equipo
     * @return true si el envío fue exitoso, false en caso contrario
     */
    public boolean enviarNotificacionEquipoAsignado(EquipoAsignadoEmailDTO equipoAsignadoDTO) {
        if (!emailEnabled) {
            log.info("Envío de correos deshabilitado. Omitiendo notificación de equipo para: {}", equipoAsignadoDTO.getEmailMiembro());
            return true;
        }

        try {
            String subject = "Has sido agregado(a) al equipo: " + equipoAsignadoDTO.getNombreEquipo();
            String content = generarPlantillaEquipoAsignado(equipoAsignadoDTO);
            
            EmailNotificationDTO notification = new EmailNotificationDTO(
                equipoAsignadoDTO.getEmailMiembro(),
                subject,
                content,
                true
            );
            
            boolean enviado = emailService.sendHtmlEmail(notification.getTo(), notification.getSubject(), notification.getContent());
            
            if (enviado) {
                log.info("Correo de asignación a equipo enviado exitosamente a: {}", equipoAsignadoDTO.getEmailMiembro());
            } else {
                log.error("Error al enviar correo de asignación a equipo a: {}", equipoAsignadoDTO.getEmailMiembro());
            }
            
            return enviado;
            
        } catch (Exception e) {
            log.error("Error al procesar correo de asignación a equipo para {}: {}", equipoAsignadoDTO.getEmailMiembro(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Genera la plantilla HTML para el correo de bienvenida.
     * 
     * @param dto Información del usuario
     * @return Contenido HTML del correo
     */
    private String generarPlantillaBienvenida(BienvenidaEmailDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' HH:mm");
        
        return "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Bienvenido(a) a " + dto.getNombreApp() + "</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                + ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }"
                + ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }"
                + ".footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }"
                + ".btn { display: inline-block; padding: 12px 24px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h1>¡Bienvenido(a) a " + dto.getNombreApp() + "!</h1>"
                + "<p>Estamos emocionados de tenerte con nosotros</p>"
                + "</div>"
                + "<div class='content'>"
                + "<h2>¡Hola " + dto.getNombreUsuario() + "!</h2>"
                + "<p>Gracias por registrarte en <strong>" + dto.getNombreApp() + "</strong>. Tu cuenta ha sido creada exitosamente y ya puedes comenzar a disfrutar de todas nuestras funcionalidades.</p>"
                + "<p><strong>Detalles de tu registro:</strong></p>"
                + "<ul>"
                + "<li><strong>Email:</strong> " + dto.getEmailUsuario() + "</li>"
                + "<li><strong>Fecha de registro:</strong> " + dto.getFechaRegistro() + "</li>"
                + "</ul>"
                + "<p>Con tu cuenta, podrás:</p>"
                + "<ul>"
                + "<li>Crear y gestionar equipos de trabajo</li>"
                + "<li>Organizar tareas y proyectos</li>"
                + "<li>Colaborar con otros miembros del equipo</li>"
                + "<li>Y mucho más...</li>"
                + "</ul>"
                + "<p>Si tienes alguna pregunta o necesitas ayuda, no dudes en contactarnos.</p>"
                + "<p style='text-align: center;'>"
                + "<a href='http://localhost:8080' class='btn'>Comenzar a usar " + dto.getNombreApp() + "</a>"
                + "</p>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>Este es un correo automático. Por favor, no respondas a este mensaje.</p>"
                + "<p>&copy; 2024 " + dto.getNombreApp() + ". Todos los derechos reservados.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    /**
     * Genera la plantilla HTML para el correo de asignación a equipo.
     * 
     * @param dto Información de la asignación
     * @return Contenido HTML del correo
     */
    private String generarPlantillaEquipoAsignado(EquipoAsignadoEmailDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' HH:mm");
        
        String content = "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Asignado a equipo - " + dto.getNombreApp() + "</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                + ".header { background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }"
                + ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }"
                + ".footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }"
                + ".btn { display: inline-block; padding: 12px 24px; background: #28a745; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }"
                + ".team-info { background: white; padding: 20px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #28a745; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h1>¡Felicidades!</h1>"
                + "<p>Has sido agregado(a) a un nuevo equipo</p>"
                + "</div>"
                + "<div class='content'>"
                + "<h2>¡Hola " + dto.getNombreMiembro() + "!</h2>"
                + "<p>Tenemos buenas noticias para ti. Has sido agregado(a) al equipo <strong>" + dto.getNombreEquipo() + "</strong> por <strong>" + dto.getNombreCreador() + "</strong>.</p>"
                
                + "<div class='team-info'>"
                + "<h3>📋 Información del equipo</h3>"
                + "<ul>"
                + "<li><strong>Nombre del equipo:</strong> " + dto.getNombreEquipo() + "</li>"
                + "<li><strong>Agregado por:</strong> " + dto.getNombreCreador() + "</li>"
                + "<li><strong>Fecha de asignación:</strong> " + dto.getFechaAsignacion() + "</li>";
        
        if (dto.getDescripcionEquipo() != null && !dto.getDescripcionEquipo().trim().isEmpty()) {
            content += "<li><strong>Descripción:</strong> " + dto.getDescripcionEquipo() + "</li>";
        }
        
        content += "</ul>"
                + "</div>"
                
                + "<p>Ahora que eres parte de este equipo, podrás:</p>"
                + "<ul>"
                + "<li>Ver las tareas y proyectos del equipo</li>"
                + "<li>Colaborar con los demás miembros</li>"
                + "<li>Recibir notificaciones importantes</li>"
                + "<li>Contribuir al éxito del equipo</li>"
                + "</ul>"
                + "<p>Accede a tu cuenta para comenzar a colaborar con tu nuevo equipo.</p>"
                + "<p style='text-align: center;'>"
                + "<a href='http://localhost:8080' class='btn'>Ir a " + dto.getNombreApp() + "</a>"
                + "</p>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>Este es un correo automático. Por favor, no respondas a este mensaje.</p>"
                + "<p>&copy; 2024 " + dto.getNombreApp() + ". Todos los derechos reservados.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        
        return content;
    }

    /**
     * Envía una notificación genérica.
     * 
     * @param notification DTO con la información del correo
     * @return true si el envío fue exitoso, false en caso contrario
     */
    public boolean enviarNotificacionGenerica(EmailNotificationDTO notification) {
        if (!emailEnabled) {
            log.info("Envío de correos deshabilitado. Omitiendo notificación para: {}", notification.getTo());
            return true;
        }

        try {
            boolean enviado = emailService.sendHtmlEmail(notification.getTo(), notification.getSubject(), notification.getContent());
            
            if (enviado) {
                log.info("Notificación genérica enviada exitosamente a: {}", notification.getTo());
            } else {
                log.error("Error al enviar notificación genérica a: {}", notification.getTo());
            }
            
            return enviado;
            
        } catch (Exception e) {
            log.error("Error al procesar notificación genérica para {}: {}", notification.getTo(), e.getMessage(), e);
            return false;
        }
    }
}
