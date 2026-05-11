package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.dto.LoginRequest;
import com.codepro.flownary_servidor.dto.LoginResponse;
import com.codepro.flownary_servidor.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para autenticación de usuarios.
 * Proporciona endpoints para login, logout y validación de tokens.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param loginRequest Credenciales del usuario (email y password)
     * @return LoginResponse con token y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Intento de login para el email: {}", loginRequest.getEmail());
            
            // Validar credenciales y generar respuesta
            LoginResponse response = usuarioService.autenticarUsuario(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            log.info("Login exitoso para el usuario: {}", response.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Credenciales inválidas para email {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(crearRespuestaError(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("Error durante el login para email {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor"));
        }
    }

    /**
     * Valida un token JWT.
     * 
     * @param request Map con el token a validar
     * @return Respuesta indicando si el token es válido
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("El token es obligatorio"));
            }
            
            // Aquí podrías agregar lógica adicional de validación
            // Por ahora, simplemente verificamos el formato del token
            if (token.startsWith("eyJ") && token.split("\\.").length == 3) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("message", "Token válido");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "Token inválido");
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al validar token"));
        }
    }

    /**
     * Cierra la sesión del usuario (placeholder para futura implementación).
     * 
     * @param request Map con el token a invalidar
     * @return Respuesta indicando el resultado del logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("El token es obligatorio"));
            }
            
            // Placeholder para futura implementación de blacklist de tokens
            log.info("Logout solicitado para token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sesión cerrada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error durante logout: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al cerrar sesión"));
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
