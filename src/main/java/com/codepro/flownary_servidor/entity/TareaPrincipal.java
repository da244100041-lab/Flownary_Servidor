package com.codepro.flownary_servidor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tareas_principales")
public class TareaPrincipal {
    
    @Id
    @Column(name = "id_tarea", columnDefinition = "CHAR(36)")
    private String idTarea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;
    
    @Column(name = "titulo", length = 150)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_kanban", nullable = false)
    private EstadoKanban estadoKanban;
    
    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "eliminado", columnDefinition = "TINYINT(1)")
    private Boolean eliminado;
    
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private List<Subtarea> subtareas;
    
    @PrePersist
    protected void onCreate() {
        if (idTarea == null) {
            idTarea = UUID.randomUUID().toString();
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
    public String getIdTarea() {
        return idTarea;
    }
    
    public void setIdTarea(String idTarea) {
        this.idTarea = idTarea;
    }
    
    public Equipo getEquipo() {
        return equipo;
    }
    
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Usuario getCreador() {
        return creador;
    }
    
    public void setCreador(Usuario creador) {
        this.creador = creador;
    }
    
    public EstadoKanban getEstadoKanban() {
        return estadoKanban;
    }
    
    public void setEstadoKanban(EstadoKanban estadoKanban) {
        this.estadoKanban = estadoKanban;
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
    
    public List<Subtarea> getSubtareas() {
        return subtareas;
    }
    
    public void setSubtareas(List<Subtarea> subtareas) {
        this.subtareas = subtareas;
    }
}
