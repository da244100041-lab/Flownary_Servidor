package com.codepro.flownary_servidor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SERVICIO DE PRUEBAS - TOKEN_ACTIVO_PARA_PRUEBAS
 * 
 * ESTE ARCHIVO ES PARA PRUEBAS TEMPORALES
 * PUEDE SER ELIMINADO CUANDO SE TERMINE LA DEPURACIÓN
 * 
 * IMPRIME TOKEN EN CONSOLA DESPUÉS DE LOGIN EXITOSO
 */
@Service
@Slf4j
public class TOKEN_ACTIVO_PARA_PRUEBAS_Service {

    @Autowired
    private SecurityService securityService;

    /**
     * Almacena y muestra el token activo para pruebas.
     * 
     * @param email Email del usuario
     * @param token Token JWT generado
     */
    public void mostrarTokenParaPruebas(String email, String token) {
        System.out.println("=================================================");
        System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - COPIAR ESTE TOKEN:");
        System.out.println("=================================================");
        System.out.println("Email: " + email);
        System.out.println("Token: " + token);
        System.out.println("=================================================");
        System.out.println("PARA USAR EN POSTMAN/INSOMNIA:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("TOKEN COMPLETO PARA COPIAR Y PEGAR:");
        System.out.println("Bearer " + token);
        System.out.println("=================================================");
        
        // También lo guarda en log
        log.info("TOKEN_ACTIVO_PARA_PRUEBAS - Email: {}, Token: {}", email, token);
    }

    /**
     * Valida un token y muestra información.
     * 
     * @param token Token a validar
     */
    public void validarTokenPrueba(String token) {
        System.out.println("=================================================");
        System.out.println("VALIDANDO TOKEN_ACTIVO_PARA_PRUEBAS:");
        System.out.println("=================================================");
        
        boolean isValid = securityService.validateSessionToken(token);
        String email = securityService.extractEmailFromToken(token);
        
        System.out.println("Token válido: " + isValid);
        System.out.println("Email extraído: " + email);
        System.out.println("=================================================");
        
        log.info("TOKEN_ACTIVO_PARA_PRUEBAS - Validación - Válido: {}, Email: {}", isValid, email);
    }
}
