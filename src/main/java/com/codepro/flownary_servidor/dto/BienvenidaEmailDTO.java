package com.codepro.flownary_servidor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO específico para el correo de bienvenida a nuevos usuarios.
 * Contiene la información personalizada para el correo de bienvenida.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BienvenidaEmailDTO {
    
    private String nombreUsuario;
    private String emailUsuario;
    private String fechaRegistro;
    private String nombreApp;
    
    public BienvenidaEmailDTO(String nombreUsuario, String emailUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.nombreApp = "Flownary";
        this.fechaRegistro = java.time.LocalDateTime.now().toString();
    }
}
