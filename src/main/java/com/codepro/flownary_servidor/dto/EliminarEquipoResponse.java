package com.codepro.flownary_servidor.dto;

/**
 * DTO para respuesta de eliminación de equipos.
 * Contiene los datos del equipo eliminado y confirmación.
 */
public class EliminarEquipoResponse {
    
    private String idEquipo;
    private String nombreEquipo;
    private String mensaje;
    private boolean exito;
    
    public EliminarEquipoResponse() {}
    
    public EliminarEquipoResponse(String idEquipo, String nombreEquipo, String mensaje, boolean exito) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.mensaje = mensaje;
        this.exito = exito;
    }
    
    public String getIdEquipo() {
        return idEquipo;
    }
    
    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }
    
    public String getNombreEquipo() {
        return nombreEquipo;
    }
    
    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public boolean isExito() {
        return exito;
    }
    
    public void setExito(boolean exito) {
        this.exito = exito;
    }
}
