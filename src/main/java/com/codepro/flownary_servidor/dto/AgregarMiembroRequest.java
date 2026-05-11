package com.codepro.flownary_servidor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitud de agregar miembros a un equipo.
 * Contiene los datos necesarios para agregar un nuevo miembro.
 * NOTA: Solo se permite agregar nuevos miembros como COLABORADOR.
 */
public class AgregarMiembroRequest {
    
    @NotBlank(message = "El ID del usuario es obligatorio")
    private String idUsuario;
    
    public AgregarMiembroRequest() {}
    
    public AgregarMiembroRequest(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
