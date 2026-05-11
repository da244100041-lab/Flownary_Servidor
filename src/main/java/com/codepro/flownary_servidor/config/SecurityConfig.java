package com.codepro.flownary_servidor.config;

import com.codepro.flownary_servidor.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad para la aplicación.
 * Define qué endpoints son públicos y cuáles requieren autenticación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura la cadena de filtros de seguridad.
     * Define las reglas de acceso para diferentes endpoints.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF ya que usamos JWT y API REST
            .csrf(csrf -> csrf.disable())
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configurar la gestión de sesiones como stateless (no guarda estado)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar las reglas de autorización
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos (no requieren autenticación)
                .requestMatchers("/", "/index", "/index.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                
                // Endpoints de API públicos
                .requestMatchers("/api/usuarios/registrar").permitAll()
                .requestMatchers("/api/usuarios/verificar-email").permitAll()
                .requestMatchers("/api/usuarios/validar-telefono").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                
                // Permitir el formulario de registro web
                .requestMatchers("/index").permitAll()
                
                // Endpoints específicos que requieren autenticación
                .requestMatchers("/api/equipos/**").authenticated()
                .requestMatchers("/api/miembros/**").authenticated()
                .requestMatchers("/api/contactos/**").authenticated()
                
                // Cualquier otra petición /api/** requiere autenticación
                .requestMatchers("/api/**").authenticated()
                
                // Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            )
            
            // Deshabilitar el formulario de login por defecto
            .formLogin(form -> form.disable())
            
            // Deshabilitar el logout por defecto (manejamos con JWT)
            .logout(logout -> logout.disable())
            
            // Agregar filtro JWT antes del filtro de autenticación de usuario
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    /**
     * Configura CORS para permitir peticiones desde el frontend.
     * Permite que tu aplicación web se comunique con la API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes (ajusta según tu frontend)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Permitir métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permitir headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Permitir credenciales (para cookies, headers de autorización, etc.)
        configuration.setAllowCredentials(true);
        
        // Exponer headers específicos si es necesario
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Tiempo máximo de preflight (en segundos)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
