package com.codepro.flownary_servidor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"equiposCreados", "equipoMiembros", "tareasCreadas", "subtareasCreadas", "mensajesEnviados"})
@ToString(exclude = {"equiposCreados", "equipoMiembros", "tareasCreadas", "subtareasCreadas", "mensajesEnviados"})
public class Usuario {
    
    @Id
    @Column(name = "id_usuario", columnDefinition = "CHAR(36)")
    private String idUsuario;
    
    @Column(name = "email", length = 150)
    private String email;
    
    @Column(name = "user_name", length = 100)
    private String userName;
    
    @Column(name = "userLasname", length = 100)
    private String userLasname;
    
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    
    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "eliminado", columnDefinition = "TINYINT(1)")
    private Boolean eliminado;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @OneToMany(mappedBy = "creador", cascade = CascadeType.ALL)
    private List<Equipo> equiposCreados;
    
    @OneToMany(mappedBy = "usuario")
    private List<EquipoMiembro> equipoMiembros;
    
    @OneToMany(mappedBy = "creador")
    private List<TareaPrincipal> tareasCreadas;
    
    @OneToMany(mappedBy = "creador")
    private List<Subtarea> subtareasCreadas;
    
    @OneToMany(mappedBy = "usuario")
    private List<MensajeChat> mensajesEnviados;
    
    @PrePersist
    protected void onCreate() {
        if (idUsuario == null) {
            idUsuario = UUID.randomUUID().toString();
        }
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
        eliminado = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
    
    // Getter para compatibilidad con el formulario HTML
    public String getPassword() {
        return passwordHash;
    }
    
    // Setter para compatibilidad con el formulario HTML
    public void setPassword(String password) {
        this.passwordHash = password;
    }
}
