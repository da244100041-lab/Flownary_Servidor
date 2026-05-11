package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para el registro y validación de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param usuario Datos del usuario a registrar
     * @return Respuesta con el usuario registrado o mensaje de error
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Validar campos obligatorios
            if (usuario.getUserName() == null || usuario.getUserName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearRespuestaError("El nombre es obligatorio"));
            }
            
            if (usuario.getUserLasname() == null || usuario.getUserLasname().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearRespuestaError("El apellido es obligatorio"));
            }
            
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearRespuestaError("El correo electrónico es obligatorio"));
            }
            
            if (usuario.getTelefono() == null || usuario.getTelefono().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearRespuestaError("El número de teléfono es obligatorio"));
            }
            
            // Registrar usuario
            Usuario usuarioGuardado = usuarioService.registrarUsuario(usuario);
            
            // Crear respuesta exitosa
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("message", "Usuario registrado exitosamente");
            respuesta.put("usuario", Map.of(
                "id", usuarioGuardado.getIdUsuario(),
                "nombre", usuarioGuardado.getUserName(),
                "apellido", usuarioGuardado.getUserLasname(),
                "email", usuarioGuardado.getEmail(),
                "telefono", usuarioGuardado.getTelefono()
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * Verifica si un email ya está registrado.
     * 
     * @param request Mapa con el email a verificar
     * @return Respuesta indicando si el email existe
     */
    @PostMapping("/verificar-email")
    public ResponseEntity<?> verificarEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearRespuestaError("El email es obligatorio"));
            }
            
            boolean existe = usuarioService.emailExiste(email.trim());
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("existe", existe);
            respuesta.put("message", existe ? "El email ya está registrado" : "El email está disponible");
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * Valida el formato de un número de teléfono.
     * 
     * @param request Mapa con el teléfono a validar
     * @return Respuesta indicando si el teléfono es válido
     */
    @PostMapping("/validar-telefono")
    public ResponseEntity<?> validarTelefono(@RequestBody Map<String, String> request) {
        try {
            String telefono = request.get("telefono");
            
            if (telefono == null || telefono.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearRespuestaError("El teléfono es obligatorio"));
            }
            
            // Validar que comience con +503 y tenga 8 dígitos adicionales
            String telefonoLimpio = telefono.replaceAll("[^+0-9]", "");
            boolean valido = telefonoLimpio.matches("^\\+503\\d{8}$");
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("valido", valido);
            respuesta.put("telefonoFormateado", valido ? telefonoLimpio : null);
            respuesta.put("message", valido ? "Teléfono válido" : "El teléfono debe comenzar con +503 y tener 8 dígitos adicionales");
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor: " + e.getMessage()));
        }
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
