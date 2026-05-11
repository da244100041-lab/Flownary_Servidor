package com.codepro.flownary_servidor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO específico para el correo de notificación de asignación a equipo.
 * Contiene la información personalizada para el correo de asignación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoAsignadoEmailDTO {
    
    private String nombreMiembro;
    private String emailMiembro;
    private String nombreEquipo;
    private String nombreCreador;
    private String descripcionEquipo;
    private String fechaAsignacion;
    private String nombreApp;
    
    public EquipoAsignadoEmailDTO(String nombreMiembro, String emailMiembro, 
                                 String nombreEquipo, String nombreCreador) {
        this.nombreMiembro = nombreMiembro;
        this.emailMiembro = emailMiembro;
        this.nombreEquipo = nombreEquipo;
        this.nombreCreador = nombreCreador;
        this.nombreApp = "Flownary";
        this.fechaAsignacion = java.time.LocalDateTime.now().toString();
        this.descripcionEquipo = "";
    }
}
