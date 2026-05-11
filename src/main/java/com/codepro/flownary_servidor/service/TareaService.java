package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.EquipoMiembro;
import com.codepro.flownary_servidor.entity.TareaPrincipal;
import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.repository.EquipoMiembroRepository;
import com.codepro.flownary_servidor.repository.EquipoRepository;
import com.codepro.flownary_servidor.repository.TareaPrincipalRepository;
import com.codepro.flownary_servidor.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de tareas.
 */
@Service
@Slf4j
public class TareaService {

    @Autowired
    private TareaPrincipalRepository tareaRepository;
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private EquipoMiembroRepository equipoMiembroRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Asigna una nueva tarea a todos los miembros activos de un equipo.
     * 
     * @param idEquipo ID del equipo
     * @param tituloTarea Título de la tarea
     * @param descripcionTarea Descripción de la tarea
     * @param idUsuarioCreador ID del usuario que crea la tarea
     * @return ResponseEntity con el resultado
     */
    public ResponseEntity<?> asignarTareaEquipo(String idEquipo, String tituloTarea, 
                                               String descripcionTarea, String idUsuarioCreador) {
        try {
            log.info("Iniciando asignación de tarea '{}' al equipo: {}", tituloTarea, idEquipo);
            
            // 1. Verificar que el equipo existe
            Equipo equipo = equipoRepository.findById(idEquipo)
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado: " + idEquipo));
            
            // 2. Verificar que el creador existe
            Usuario creador = usuarioRepository.findById(idUsuarioCreador)
                    .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado: " + idUsuarioCreador));
            
            // 3. Obtener todos los miembros activos del equipo
            List<EquipoMiembro> miembrosActivos = equipoMiembroRepository
                    .findMiembrosActivosByEquipoId(idEquipo);
            
            if (miembrosActivos.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No hay miembros activos en el equipo");
            }
            
            log.info("Se encontraron {} miembros activos en el equipo", miembrosActivos.size());
            
            // 4. Crear la tarea principal
            TareaPrincipal nuevaTarea = new TareaPrincipal();
            nuevaTarea.setIdTarea(UUID.randomUUID().toString());
            nuevaTarea.setTitulo(tituloTarea);
            nuevaTarea.setDescripcion(descripcionTarea);
            nuevaTarea.setEquipo(equipo);
            nuevaTarea.setCreador(creador);
            nuevaTarea.setFechaCreacion(LocalDateTime.now());
            nuevaTarea.setEliminado(false);
            
            // 5. Guardar la tarea en la base de datos
            TareaPrincipal tareaGuardada = tareaRepository.save(nuevaTarea);
            
            log.info("Tarea guardada exitosamente con ID: {}", tareaGuardada.getIdTarea());
            
            // 6. Tarea asignada exitosamente (notificaciones se manejarán por otros medios)
            log.info("Tarea asignada a {} miembros del equipo '{}'", 
                    miembrosActivos.size() - 1, equipo.getNombre());
            
            // 7. Retornar respuesta exitosa
            return ResponseEntity.ok().body(
                "Tarea '" + tituloTarea + "' asignada exitosamente a " + 
                (miembrosActivos.size() - 1) + " miembros del equipo '" + equipo.getNombre() + "'"
            );
            
        } catch (Exception e) {
            log.error("Error al asignar tarea al equipo {}: {}", idEquipo, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Error al asignar tarea: " + e.getMessage());
        }
    }
}
