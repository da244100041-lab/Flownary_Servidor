package com.codepro.flownary_servidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un miembro de un equipo.
 * Contiene información del usuario y su rol en el equipo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiembroEquipoDTO {
    private String idUsuario;
    private String nombreCompleto;
    private String email;
    private String rol;
}
