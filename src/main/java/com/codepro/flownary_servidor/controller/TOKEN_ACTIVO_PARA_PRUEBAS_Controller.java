package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.dto.LoginRequest;
import com.codepro.flownary_servidor.dto.LoginResponse;
import com.codepro.flownary_servidor.service.SecurityService;
import com.codepro.flownary_servidor.service.UsuarioService;
import com.codepro.flownary_servidor.service.TOKEN_ACTIVO_PARA_PRUEBAS_Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLADOR DE PRUEBAS - TOKEN_ACTIVO_PARA_PRUEBAS
 * 
 * ESTE ARCHIVO ES PARA PRUEBAS TEMPORALES
 * PUEDE SER ELIMINADO CUANDO SE TERMINE LA DEPURACIÓN
 * 
 * ENDPOINTS PARA PROBAR TOKEN Y VALIDACIÓN
 */
@RestController
@RequestMapping("/api/TOKEN_ACTIVO_PARA_PRUEBAS")
@CrossOrigin(origins = "*")
@Slf4j
public class TOKEN_ACTIVO_PARA_PRUEBAS_Controller {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TOKEN_ACTIVO_PARA_PRUEBAS_Service tokenPruebasService;

    /**
     * Endpoint de login que muestra el token en consola.
     * 
     * @param loginRequest Credenciales de login
     * @return LoginResponse con token mostrado en consola
     */
    @PostMapping("/login-con-token")
    public ResponseEntity<?> loginConToken(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - INICIANDO LOGIN");
            System.out.println("=================================================");
            System.out.println("Email: " + loginRequest.getEmail());
            System.out.println("=================================================");
            
            // Autenticar usuario
            LoginResponse response = usuarioService.autenticarUsuario(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            // Mostrar token en consola
            tokenPruebasService.mostrarTokenParaPruebas(loginRequest.getEmail(), response.getToken());
            
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - LOGIN EXITOSO");
            System.out.println("=================================================");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - ERROR EN LOGIN");
            System.out.println("Error: " + e.getMessage());
            System.out.println("=================================================");
            
            log.error("Error en login con token: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error en login: " + e.getMessage());
        }
    }

    /**
     * Endpoint para validar un token.
     * 
     * @param token Token a validar
     * @return Resultado de validación
     */
    @PostMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestBody String token) {
        try {
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - VALIDANDO TOKEN");
            System.out.println("=================================================");
            
            // Validar token
            tokenPruebasService.validarTokenPrueba(token);
            
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - VALIDACIÓN COMPLETADA");
            System.out.println("=================================================");
            
            return ResponseEntity.ok("Token validado exitosamente");
            
        } catch (Exception e) {
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - ERROR EN VALIDACIÓN");
            System.out.println("Error: " + e.getMessage());
            System.out.println("=================================================");
            
            log.error("Error en validación de token: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error en validación: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obtener información del token actual.
     * 
     * @return Información del token
     */
    @GetMapping("/info-token")
    public ResponseEntity<?> infoToken() {
        try {
            System.out.println("=================================================");
            System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - OBTENIENDO INFO TOKEN");
            System.out.println("=================================================");
            
            // Aquí podrías obtener el token del request actual
            // y mostrar información útil para depuración
            System.out.println("Para obtener info del token, usa el endpoint POST /api/TOKEN_ACTIVO_PARA_PRUEBAS/validar-token");
            System.out.println("=================================================");
            
            return ResponseEntity.ok("Usa POST /api/TOKEN_ACTIVO_PARA_PRUEBAS/validar-token para validar un token");
            
        } catch (Exception e) {
            log.error("Error al obtener info del token: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
