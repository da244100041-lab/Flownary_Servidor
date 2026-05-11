package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.dto.ContactosRequest;
import com.codepro.flownary_servidor.dto.ContactosResponse;
import com.codepro.flownary_servidor.service.ContactosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para la gestión de contactos.
 * Proporciona endpoints para verificar qué usuarios están registrados por teléfono.
 */
@RestController
@RequestMapping("/api/contactos")
@CrossOrigin(origins = "*")
@Slf4j
public class ContactosController {

    @Autowired
    private ContactosService contactosService;

    /**
     * Verifica qué números de teléfono están registrados en el sistema.
     * Requiere autenticación mediante token JWT.
     * 
     * @param request Solicitud con lista de números de teléfono a verificar
     * @return Respuesta con los contactos encontrados
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarContactos(@RequestBody ContactosRequest request) {
        try {
            log.info("Recibida solicitud de verificación de {} contactos", 
                    request.getTelefonos() != null ? request.getTelefonos().size() : 0);

            // Validar que la lista no esté vacía
            if (request.getTelefonos() == null || request.getTelefonos().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("La lista de teléfonos no puede estar vacía"));
            }

            // Validar que no haya demasiados números (para evitar sobrecarga)
            if (request.getTelefonos().size() > 100) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("No se pueden verificar más de 100 números a la vez"));
            }

            // Delegar al servicio de contactos
            ContactosResponse response = contactosService.verificarContactos(request);

            log.info("Verificación completada: {} contactos encontrados de {} solicitados", 
                    response.getTotalEncontrados(), response.getTotalSolicitados());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en verificación de contactos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));

        } catch (Exception e) {
            log.error("Error interno al verificar contactos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor al procesar la solicitud"));
        }
    }

    /**
     * Endpoint de prueba para verificar que el servicio está funcionando.
     * 
     * @return Mensaje de estado del servicio
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Servicio de contactos funcionando correctamente");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta de error estandarizada.
     * 
     * @param mensaje Mensaje de error
     * @return Mapa con la estructura de error
     */
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", true);
        error.put("message", mensaje);
        return error;
    }
}
