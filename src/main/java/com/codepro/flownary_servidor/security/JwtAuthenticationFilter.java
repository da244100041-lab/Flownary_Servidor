package com.codepro.flownary_servidor.security;

import com.codepro.flownary_servidor.service.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT para autenticación de requests.
 * Interceptor que valida tokens JWT en cada request protegida.
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extraer token JWT del header Authorization
            String jwt = getJwtFromRequest(request);
            
            // Validar token y establecer autenticación
            if (StringUtils.hasText(jwt) && securityService.validateSessionToken(jwt)) {
                // Extraer email del token
                String email = securityService.extractEmailFromToken(jwt);
                
                // Crear token de autenticación
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        email, 
                        null, 
                        java.util.Collections.emptyList()
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Establecer autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Autenticación JWT establecida para usuario: {}", email);
            } else {
                log.debug("No se encontró token JWT válido en la request");
            }
            
        } catch (Exception ex) {
            log.error("No se pudo procesar la autenticación JWT", ex);
        }
        
        // Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization.
     * 
     * @param request Request HTTP
     * @return Token JWT o null si no existe
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // Validar que el header existe y comienza con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remover "Bearer "
        }
        
        return null;
    }
}
