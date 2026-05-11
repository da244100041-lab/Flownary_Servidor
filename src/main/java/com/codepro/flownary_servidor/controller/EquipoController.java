package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.dto.ContactosRequest;
import com.codepro.flownary_servidor.dto.CrearEquipoRequest;
import com.codepro.flownary_servidor.dto.EquipoUsuarioDTO;
import com.codepro.flownary_servidor.dto.AgregarMiembroRequest;
import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.service.EquipoService;
import com.codepro.flownary_servidor.service.UsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de equipos.
 * Proporciona endpoints para consultar equipos del usuario y verificar contactos.
 */
@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
@Slf4j
public class EquipoController {

    @Autowired
    private EquipoService equipoService;
    
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene los equipos del usuario autenticado con estadísticas.
     * Requiere autenticación mediante token JWT.
     * 
     * @param authentication Objeto de autenticación del usuario
     * @return Respuesta con los equipos y sus estadísticas
     */
    @GetMapping("/mis-equipos")
    public ResponseEntity<?> obtenerMisEquipos(Authentication authentication) {
        try {
            // Obtener email del usuario autenticado
            String email = authentication.getName();
            log.info("Solicitando equipos para usuario autenticado: {}", email);

            // Buscar usuario en la base de datos
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", true);
                error.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Obtener equipos con estadísticas
            List<EquipoUsuarioDTO> equipos = equipoService.obtenerEquiposConEstadisticas(usuario);

            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("equipos", equipos);
            response.put("totalEquipos", equipos.size());
            response.put("message", "Se encontraron " + equipos.size() + " equipos");

            log.info("Retornando {} equipos para usuario: {}", equipos.size(), email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener equipos del usuario: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", true);
            error.put("message", "Error interno del servidor al obtener los equipos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Verifica qué números de teléfono están registrados en el sistema.
     * Requiere autenticación mediante token JWT.
     * 
     * @param request Solicitud con lista de números de teléfono a verificar
     * @return Respuesta con los contactos encontrados
     */
    @PostMapping("/verificar-contactos")
    public ResponseEntity<?> verificarContactos(@RequestBody ContactosRequest request) {
        log.info("Recibida solicitud de verificación de contactos en EquipoController");
        return equipoService.verificarContactos(request);
    }

    /**
     * Crea un nuevo equipo y registra al creador como administrador.
     * 
     * @param request Datos del equipo a crear
     * @return ResponseEntity con el resultado de la operación
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearEquipo(@RequestBody CrearEquipoRequest request) {
        try {
            log.info("Solicitud para crear equipo: {}", request.getNombre());
            
            // Obtener usuario autenticado
            String emailUsuarioAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            if (emailUsuarioAutenticado == null) {
                log.warn("Intento de crear equipo sin autenticación");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario no autenticado"));
            }

            // Buscar usuario autenticado
            Usuario usuarioCreador = usuarioService.buscarPorEmail(emailUsuarioAutenticado);
            if (usuarioCreador == null) {
                log.warn("Usuario autenticado no encontrado: {}", emailUsuarioAutenticado);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario autenticado no encontrado"));
            }

            // Crear equipo usando el servicio
            return equipoService.crearEquipo(request, usuarioCreador);
            
        } catch (Exception e) {
            log.error("Error al procesar solicitud de crear equipo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    /**
     * Crea una respuesta de error estandarizada.
     * 
     * @param mensaje Mensaje de error
     * @return Mapa con la estructura de error
     */
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", true);
        error.put("message", mensaje);
        return error;
    }

    /**
     * Elimina un equipo verificando que el usuario sea administrador.
     * 
     * @param idEquipo ID del equipo a eliminar
     * @return ResponseEntity con el resultado de la operación
     */
    @DeleteMapping("/eliminar/{idEquipo}")
    public ResponseEntity<?> eliminarEquipo(@PathVariable String idEquipo) {
        try {
            log.info("Solicitud para eliminar equipo: {}", idEquipo);
            
            // Obtener usuario autenticado
            String emailUsuarioAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            if (emailUsuarioAutenticado == null) {
                log.warn("Intento de eliminar equipo sin autenticación");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario no autenticado"));
            }

            // Buscar usuario autenticado
            Usuario usuarioSolicitante = usuarioService.buscarPorEmail(emailUsuarioAutenticado);
            if (usuarioSolicitante == null) {
                log.warn("Usuario autenticado no encontrado: {}", emailUsuarioAutenticado);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario autenticado no encontrado"));
            }

            // Eliminar equipo usando el servicio
            return equipoService.eliminarEquipo(idEquipo, usuarioSolicitante);
            
        } catch (Exception e) {
            log.error("Error al procesar solicitud de eliminar equipo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    /**
     * Agrega un miembro a un equipo verificando que el solicitante sea administrador.
     * 
     * @param idEquipo ID del equipo
     * @param request Datos del miembro a agregar
     * @return ResponseEntity con el resultado de la operación
     */
    @PostMapping("/agregar-miembro/{idEquipo}")
    public ResponseEntity<?> agregarMiembro(@PathVariable String idEquipo, @RequestBody AgregarMiembroRequest request) {
        try {
            log.info("Solicitud para agregar miembro {} al equipo: {}", request.getIdUsuario(), idEquipo);
            
            // Obtener usuario autenticado
            String emailUsuarioAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            if (emailUsuarioAutenticado == null) {
                log.warn("Intento de agregar miembro sin autenticación");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario no autenticado"));
            }

            // Buscar usuario autenticado
            Usuario usuarioSolicitante = usuarioService.buscarPorEmail(emailUsuarioAutenticado);
            if (usuarioSolicitante == null) {
                log.warn("Usuario autenticado no encontrado: {}", emailUsuarioAutenticado);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario autenticado no encontrado"));
            }

            // Agregar miembro usando el servicio
            return equipoService.agregarMiembro(idEquipo, request, usuarioSolicitante);
            
        } catch (Exception e) {
            log.error("Error al procesar solicitud de agregar miembro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    /**
     * Endpoint de prueba para verificar que el servicio está funcionando.
     * 
     * @return Mensaje de estado del servicio
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        log.info("Solicitud de status en EquipoController");
        return equipoService.obtenerStatus();
    }
}
