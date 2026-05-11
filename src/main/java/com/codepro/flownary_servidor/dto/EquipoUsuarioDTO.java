package com.codepro.flownary_servidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un equipo del usuario con estadísticas.
 * Contiene información básica del equipo y conteos de miembros y tareas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoUsuarioDTO {
    private String idEquipo;
    private String nombre;
    private int cantidadMiembros;
    private int cantidadTareas;
}
