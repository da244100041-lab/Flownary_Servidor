package com.codepro.flownary_servidor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "equipos")
public class Equipo {
    
    @Id
    @Column(name = "id_equipo", columnDefinition = "CHAR(36)")
    private String idEquipo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;
    
    @Column(name = "nombre", length = 150)
    private String nombre;
    
    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "eliminado", columnDefinition = "TINYINT(1)")
    private Boolean eliminado;
    
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    private List<EquipoMiembro> miembros;
    
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    private List<TareaPrincipal> tareas;
    
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    private List<MensajeChat> mensajes;
    
    @PrePersist
    protected void onCreate() {
        if (idEquipo == null) {
            idEquipo = UUID.randomUUID().toString();
        }
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
        eliminado = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getIdEquipo() {
        return idEquipo;
    }
    
    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }
    
    public Usuario getCreador() {
        return creador;
    }
    
    public void setCreador(Usuario creador) {
        this.creador = creador;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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
    
    public List<EquipoMiembro> getMiembros() {
        return miembros;
    }
    
    public void setMiembros(List<EquipoMiembro> miembros) {
        this.miembros = miembros;
    }
    
    public List<TareaPrincipal> getTareas() {
        return tareas;
    }
    
    public void setTareas(List<TareaPrincipal> tareas) {
        this.tareas = tareas;
    }
    
    public List<MensajeChat> getMensajes() {
        return mensajes;
    }
    
    public void setMensajes(List<MensajeChat> mensajes) {
        this.mensajes = mensajes;
    }
}
