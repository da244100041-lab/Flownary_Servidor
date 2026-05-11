package com.codepro.flownary_servidor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitud de creación de equipos.
 * Contiene los datos necesarios para crear un nuevo equipo.
 */
public class CrearEquipoRequest {
    
    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre del equipo debe tener entre 3 y 150 caracteres")
    private String nombre;
    
    public CrearEquipoRequest() {}
    
    public CrearEquipoRequest(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
