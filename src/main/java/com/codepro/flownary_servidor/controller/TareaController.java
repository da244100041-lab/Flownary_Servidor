package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.service.TareaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para la gestión de tareas y asignación a equipos.
 */
@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "*")
@Slf4j
public class TareaController {

    @Autowired
    private TareaService tareaService;

    /**
     * Asigna una nueva tarea a todos los miembros activos de un equipo.
     * 
     * @param request Map con los datos de la tarea
     * @return ResponseEntity con el resultado
     */
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarTarea(@RequestBody Map<String, String> request) {
        try {
            String idEquipo = request.get("idEquipo");
            String tituloTarea = request.get("tituloTarea");
            String descripcionTarea = request.get("descripcionTarea");
            String idUsuarioCreador = request.get("idUsuarioCreador");

            // Validaciones básicas
            if (idEquipo == null || idEquipo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID del equipo es requerido");
            }
            if (tituloTarea == null || tituloTarea.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Título de la tarea es requerido");
            }
            if (idUsuarioCreador == null || idUsuarioCreador.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID del usuario creador es requerido");
            }

            log.info("Solicitud de asignación de tarea: '{}' al equipo '{}' por usuario '{}'", 
                    tituloTarea, idEquipo, idUsuarioCreador);

            // Llamar al servicio para asignar la tarea
            ResponseEntity<?> resultado = tareaService.asignarTareaEquipo(
                    idEquipo, tituloTarea, descripcionTarea, idUsuarioCreador);

            return resultado;

        } catch (Exception e) {
            log.error("Error en el controlador al asignar tarea: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    /**
     * Endpoint de prueba para verificar que el controlador funciona.
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok().body(Map.of(
                "status", "TareaController funcionando",
                "timestamp", System.currentTimeMillis(),
                "endpoints", Map.of(
                        "POST /api/tareas/asignar", "Asignar tarea a equipo",
                        "GET /api/tareas/status", "Verificar estado"
                )
        ));
    }
}
