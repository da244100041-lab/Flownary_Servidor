package com.codepro.flownary_servidor.dto;

import lombok.Data;

/**
 * DTO para solicitudes relacionadas con equipos.
 * Puede ser extendido para diferentes operaciones de equipos.
 */
@Data
public class EquipoRequest {
    private String idEquipo;
    private String nombre;
    private String accion; // Para especificar la acción a realizar
    
    // Constructor para solicitudes simples
    public EquipoRequest() {}
    
    public EquipoRequest(String accion) {
        this.accion = accion;
    }
    
    public EquipoRequest(String idEquipo, String accion) {
        this.idEquipo = idEquipo;
        this.accion = accion;
    }
}
