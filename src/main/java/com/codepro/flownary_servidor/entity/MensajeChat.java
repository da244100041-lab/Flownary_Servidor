package com.codepro.flownary_servidor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensajes_chat")
public class MensajeChat {
    
    @Id
    @Column(name = "id_mensaje", columnDefinition = "CHAR(36)")
    private String idMensaje;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;
    
    @Column(name = "fecha_envio", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "fecha_modificacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "eliminado", columnDefinition = "TINYINT(1)")
    private Boolean eliminado;
    
    @PrePersist
    protected void onCreate() {
        if (idMensaje == null) {
            idMensaje = UUID.randomUUID().toString();
        }
        fechaEnvio = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
        eliminado = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getIdMensaje() {
        return idMensaje;
    }
    
    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }
    
    public Equipo getEquipo() {
        return equipo;
    }
    
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }
    
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    
    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
    
    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
    
    public Boolean getEliminado() {
        return eliminado;
    }
    
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}
