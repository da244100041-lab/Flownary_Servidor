package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.service.MiembrosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de miembros de equipos.
 * Proporciona endpoints para consultar miembros activos de un equipo.
 */
@RestController
@RequestMapping("/api/miembros")
@CrossOrigin(origins = "*")
@Slf4j
public class MiembrosController {

    @Autowired
    private MiembrosService miembrosService;

    /**
     * Obtiene los miembros activos de un equipo.
     * Requiere autenticación mediante token JWT.
     * 
     * @param idEquipo ID del equipo a consultar (parámetro de path)
     * @return Respuesta con la lista de miembros activos del equipo
     */
    @GetMapping("/equipo/{idEquipo}")
    public ResponseEntity<?> obtenerMiembrosPorEquipo(@PathVariable String idEquipo) {
        log.info("Solicitando miembros del equipo: {}", idEquipo);
        return miembrosService.obtenerMiembrosActivos(idEquipo);
    }

    /**
     * Endpoint de prueba para verificar que el servicio está funcionando.
     * 
     * @return Mensaje de estado del servicio
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        log.info("Solicitud de status en MiembrosController");
        
        var response = new java.util.HashMap<String, Object>();
        response.put("success", true);
        response.put("message", "Servicio de miembros funcionando correctamente");
        response.put("timestamp", System.currentTimeMillis());
        response.put("servicio", "MiembrosService");
        
        return ResponseEntity.ok(response);
    }
}
