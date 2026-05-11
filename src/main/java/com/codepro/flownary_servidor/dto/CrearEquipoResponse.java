package com.codepro.flownary_servidor.dto;

/**
 * DTO para respuesta de creación de equipos.
 * Contiene los datos del equipo creado y confirmación.
 */
public class CrearEquipoResponse {
    
    private String idEquipo;
    private String nombre;
    private String mensaje;
    private boolean exito;
    
    public CrearEquipoResponse() {}
    
    public CrearEquipoResponse(String idEquipo, String nombre, String mensaje, boolean exito) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.exito = exito;
    }
    
    public String getIdEquipo() {
        return idEquipo;
    }
    
    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
