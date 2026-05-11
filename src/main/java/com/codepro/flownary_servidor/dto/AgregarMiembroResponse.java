package com.codepro.flownary_servidor.dto;

/**
 * DTO para respuesta de agregar miembros a un equipo.
 * Contiene los datos del miembro agregado y confirmación.
 */
public class AgregarMiembroResponse {
    
    private String idMiembro;
    private String idUsuario;
    private String emailMiembro;
    private String nombreMiembro;
    private String rol;
    private String nombreEquipo;
    private String mensaje;
    private boolean exito;
    
    public AgregarMiembroResponse() {}
    
    public AgregarMiembroResponse(String idMiembro, String idUsuario, String emailMiembro, String nombreMiembro, 
                               String rol, String nombreEquipo, String mensaje, boolean exito) {
        this.idMiembro = idMiembro;
        this.idUsuario = idUsuario;
        this.emailMiembro = emailMiembro;
        this.nombreMiembro = nombreMiembro;
        this.rol = rol;
        this.nombreEquipo = nombreEquipo;
        this.mensaje = mensaje;
        this.exito = exito;
    }
    
    public String getIdMiembro() {
        return idMiembro;
    }
    
    public void setIdMiembro(String idMiembro) {
        this.idMiembro = idMiembro;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getEmailMiembro() {
        return emailMiembro;
    }
    
    public void setEmailMiembro(String emailMiembro) {
        this.emailMiembro = emailMiembro;
    }
    
    public String getNombreMiembro() {
        return nombreMiembro;
    }
    
    public void setNombreMiembro(String nombreMiembro) {
        this.nombreMiembro = nombreMiembro;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
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
