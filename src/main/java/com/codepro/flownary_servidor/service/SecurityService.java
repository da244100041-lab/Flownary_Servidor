package com.codepro.flownary_servidor.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio dedicado a operaciones de seguridad.
 * Centraliza el hashing de contraseñas, validación y generación de tokens JWT.
 */
@Service
@Slf4j
public class SecurityService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecretKey jwtSecretKey;



    @Value("${app.jwt.expiration:86400000}") // Un dia para la duracion por defecto para el token.

   // @Value("${app.jwt.expiration:3600000}") // Una hora para la duracion por defecto para el token.

    private long jwtExpirationMs;
    
    public SecurityService() {
        this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        log.info("SecurityService inicializado con clave JWT segura");
    }

    /**
     * Hashea una contraseña usando BCrypt.
     * 
     * @param password Contraseña en texto plano
     * @return Contraseña hasheada
     */
    public String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        
        String hashedPassword = passwordEncoder.encode(password);
        log.debug("Contraseña hasheada exitosamente");
        return hashedPassword;
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     * 
     * @param plainPassword Contraseña en texto plano
     * @param hashedPassword Hash almacenado en la base de datos
     * @return true si la contraseña coincide, false en caso contrario
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            log.warn("Intento de verificación con contraseña o hash nulo");
            return false;
        }
        
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        log.debug("Verificación de contraseña: {}", matches ? "exitosa" : "fallida");
        return matches;
    }

    /**
     * Genera un token JWT para el usuario.
     * 
     * @param email Email del usuario
     * @return Token JWT
     */
    public String generateSessionToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
        
        log.info("Token JWT generado para usuario: {}", email);
        return token;
    }

    /**
     * Valida un token JWT.
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateSessionToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token);
            log.debug("Token JWT válido");
            return true;
        } catch (Exception e) {
            log.debug("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrae el email del token JWT.
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    public String extractEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrae la fecha de expiración del token JWT.
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date extractExpirationFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrae un claim específico del token JWT.
     * 
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim
     * @return Valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrae todos los claims del token JWT.
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Verifica si el token JWT está expirado.
     * 
     * @param token Token JWT
     * @return true si está expirado, false en caso contrario
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpirationFromToken(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Genera un hash para verificación de integridad de datos.
     * 
     * @param data Datos a hashear
     * @return Hash de los datos
     */
    public String generateDataHash(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Los datos no pueden ser nulos");
        }
        
        // Usar el mismo passwordEncoder para consistencia
        String hash = passwordEncoder.encode(data);
        log.debug("Hash de datos generado exitosamente");
        return hash;
    }
}
