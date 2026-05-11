package com.codepro.flownary_servidor.dto;

import lombok.Data;

/**
 * DTO para respuesta de autenticación exitosa.
 */
@Data
public class LoginResponse {
    private String token;
    private String email;
    private String nombre;
    private String apellido;
    private String message;
    
    public LoginResponse(String token, String email, String nombre, String apellido) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.message = "Autenticación exitosa";
    }
}
