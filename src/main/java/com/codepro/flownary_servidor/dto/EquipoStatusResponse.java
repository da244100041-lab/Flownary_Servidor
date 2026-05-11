package com.codepro.flownary_servidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de estado del servicio de equipos.
 * Contiene información sobre el estado y timestamp.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoStatusResponse {
    private boolean success;
    private String message;
    private long timestamp;
    private String servicio;
    
    public EquipoStatusResponse(String message) {
        this.success = true;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.servicio = "EquipoService";
    }
    
    public EquipoStatusResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.servicio = "EquipoService";
    }
}
