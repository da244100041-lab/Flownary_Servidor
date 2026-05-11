package com.codepro.flownary_servidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de consulta de equipos del usuario.
 * Contiene la lista de equipos con estadísticas y metadatos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquiposResponse {
    private List<EquipoUsuarioDTO> equipos;
    private int totalEquipos;
    private String message;
    private boolean success;
    
    public EquiposResponse(List<EquipoUsuarioDTO> equipos, String message) {
        this.equipos = equipos;
        this.totalEquipos = equipos != null ? equipos.size() : 0;
        this.message = message;
        this.success = true;
    }
    
    public EquiposResponse(List<EquipoUsuarioDTO> equipos) {
        this(equipos, "Se encontraron " + (equipos != null ? equipos.size() : 0) + " equipos");
    }
}
