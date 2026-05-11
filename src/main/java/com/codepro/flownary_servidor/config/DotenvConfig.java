package com.codepro.flownary_servidor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para cargar variables de entorno desde archivo .env
 * Solo se activa en perfiles que no son de test
 */
@Configuration
public class DotenvConfig {

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void configure() {
        // No cargar variables .env en perfil de test
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("test".equals(profile)) {
                System.out.println("Perfil 'test' detectado - omitiendo carga de variables .env");
                return;
            }
        }
        
        try {
            // Cargar variables desde el archivo .env manualmente
            Map<String, Object> dotenvProperties = loadEnvFile(".env");
            
            // Crear PropertySource y agregarlo al entorno de Spring
            PropertySource<?> dotenvPropertySource = new MapPropertySource("dotenv", dotenvProperties);
            environment.getPropertySources().addFirst(dotenvPropertySource);
            
            System.out.println("Variables .env cargadas correctamente con " + dotenvProperties.size() + " variables");
            
        } catch (Exception e) {
            System.err.println("Error al cargar variables .env: " + e.getMessage());
            // No lanzar excepción para no interrumpir el inicio
        }
    }

    private Map<String, Object> loadEnvFile(String filename) throws IOException {
        Map<String, Object> properties = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Procesar líneas con formato KEY=VALUE
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    // Remover comillas si existen
                    if ((value.startsWith("\"") && value.endsWith("\"")) || 
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    
                    properties.put(key, value);
                }
            }
        }
        
        return properties;
    }
}
