package com.codepro.flownary_servidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un contacto encontrado en la base de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoEncontrado {
    private String idUsuario;
    private String nombreCompleto;
    private String email;
    private String telefono;
    
    public ContactoEncontrado(String idUsuario, String nombre, String apellido, String email, String telefono) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombre + " " + apellido;
        this.email = email;
        this.telefono = telefono;
    }
}
