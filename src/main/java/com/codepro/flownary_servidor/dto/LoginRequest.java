package com.codepro.flownary_servidor.dto;

import lombok.Data;

/**
 * DTO para solicitar autenticación de usuario.
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
