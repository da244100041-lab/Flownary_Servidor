package com.codepro.flownary_servidor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subtareas")
public class Subtarea {
    
    @Id
    @Column(name = "id_subtarea", columnDefinition = "CHAR(36)")
    private String idSubtarea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarea", nullable = false)
    private TareaPrincipal tarea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;
    
    @Column(name = "titulo", length = 200)
    private String titulo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_kanban", nullable = false)
    private EstadoKanban estadoKanban;
    
    @Column(name = "orden")
    private Integer orden;
    
    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "eliminado", columnDefinition = "TINYINT(1)")
    private Boolean eliminado;
    
    @PrePersist
    protected void onCreate() {
        if (idSubtarea == null) {
            idSubtarea = UUID.randomUUID().toString();
        }
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
        eliminado = false;
        if (estadoKanban == null) {
            estadoKanban = EstadoKanban.PENDIENTE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getIdSubtarea() {
        return idSubtarea;
    }
    
    public void setIdSubtarea(String idSubtarea) {
        this.idSubtarea = idSubtarea;
    }
    
    public TareaPrincipal getTarea() {
        return tarea;
    }
    
    public void setTarea(TareaPrincipal tarea) {
        this.tarea = tarea;
    }
    
    public Usuario getCreador() {
        return creador;
    }
    
    public void setCreador(Usuario creador) {
        this.creador = creador;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public EstadoKanban getEstadoKanban() {
        return estadoKanban;
    }
    
    public void setEstadoKanban(EstadoKanban estadoKanban) {
        this.estadoKanban = estadoKanban;
    }
    
    public Integer getOrden() {
        return orden;
    }
    
    public void setOrden(Integer orden) {
        this.orden = orden;
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
}
