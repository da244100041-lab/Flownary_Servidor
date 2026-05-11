package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.dto.ContactosRequest;
import com.codepro.flownary_servidor.dto.ContactosResponse;
import com.codepro.flownary_servidor.dto.CrearEquipoRequest;
import com.codepro.flownary_servidor.dto.CrearEquipoResponse;
import com.codepro.flownary_servidor.dto.EliminarEquipoResponse;
import com.codepro.flownary_servidor.dto.EquipoUsuarioDTO;
import com.codepro.flownary_servidor.dto.AgregarMiembroRequest;
import com.codepro.flownary_servidor.dto.AgregarMiembroResponse;
import com.codepro.flownary_servidor.dto.EquipoAsignadoEmailDTO;
import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.EquipoMiembro;
import com.codepro.flownary_servidor.entity.Rol;
import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.repository.EquipoRepository;
import com.codepro.flownary_servidor.repository.EquipoMiembroRepository;
import com.codepro.flownary_servidor.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para consultar equipos de un usuario con estadísticas.
 * Proporciona información sobre equipos, miembros y tareas asociadas.
 */
@Service
@Slf4j
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private EquipoMiembroRepository equipoMiembroRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ContactosService contactosService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private NotificacionService notificacionService;

    /**
     * Obtiene todos los equipos de un usuario con estadísticas.
     * Incluye equipos creados y equipos donde es miembro.
     * 
     * @param usuario Usuario para consultar sus equipos
     * @return Lista de equipos con cantidad de miembros y tareas
     */
    public List<EquipoUsuarioDTO> obtenerEquiposConEstadisticas(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        log.info("Consultando equipos para usuario: {}", usuario.getEmail());

        // Obtener equipos donde el usuario es creador o miembro activo
        List<Equipo> equipos = equipoRepository.findEquiposByUsuario(usuario.getIdUsuario());
        log.info("Se encontraron {} equipos para el usuario", equipos.size());

        // Convertir equipos a DTOs con estadísticas
        List<EquipoUsuarioDTO> equiposDTO = new ArrayList<>();
        for (Equipo equipo : equipos) {
            // Calcular cantidad de miembros activos
            int cantidadMiembros = contarMiembrosActivos(equipo);
            
            // Calcular cantidad de tareas
            int cantidadTareas = contarTareas(equipo);
            
            // Crear DTO con estadísticas
            EquipoUsuarioDTO equipoDTO = new EquipoUsuarioDTO(
                equipo.getIdEquipo(),
                equipo.getNombre(),
                cantidadMiembros,
                cantidadTareas
            );
            
            equiposDTO.add(equipoDTO);
            
            log.debug("Equipo {}: {} miembros, {} tareas", 
                    equipo.getNombre(), cantidadMiembros, cantidadTareas);
        }

        log.info("Retornando {} equipos con estadísticas", equiposDTO.size());
        return equiposDTO;
    }

    /**
     * Cuenta los miembros activos de un equipo.
     * 
     * @param equipo Equipo para contar sus miembros
     * @return Cantidad de miembros activos
     */
    private int contarMiembrosActivos(Equipo equipo) {
        if (equipo.getMiembros() == null) {
            return 0;
        }
        
        return (int) equipo.getMiembros().stream()
                .filter(miembro -> miembro.getEliminado() == null || !miembro.getEliminado())
                .count();
    }

    /**
     * Cuenta las tareas asignadas a un equipo.
     * 
     * @param equipo Equipo para contar sus tareas
     * @return Cantidad de tareas
     */
    private int contarTareas(Equipo equipo) {
        if (equipo.getTareas() == null) {
            return 0;
        }
        
        return equipo.getTareas().size();
    }

    /**
     * Verifica contactos telefónicos usando ContactosService.
     * Centraliza la lógica de validación y delega al servicio especializado.
     * 
     * @param request Solicitud con lista de teléfonos
     * @return ResponseEntity con resultado de la verificación
     */
    public ResponseEntity<?> verificarContactos(ContactosRequest request) {
        try {
            log.info("Recibida solicitud de verificación de {} contactos", 
                    request.getTelefonos() != null ? request.getTelefonos().size() : 0);

            // Validar que la lista no esté vacía
            if (request.getTelefonos() == null || request.getTelefonos().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("La lista de teléfonos no puede estar vacía"));
            }

            // Validar que no haya demasiados números (para evitar sobrecarga)
            if (request.getTelefonos().size() > 100) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("No se pueden verificar más de 100 números a la vez"));
            }

            // Delegar al servicio especializado
            ContactosResponse response = contactosService.verificarContactos(request);

            log.info("Verificación completada: {} contactos encontrados de {} solicitados", 
                    response.getTotalEncontrados(), response.getTotalSolicitados());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en verificación de contactos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));

        } catch (Exception e) {
            log.error("Error interno al verificar contactos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor al procesar la solicitud"));
        }
    }

    /**
     * Obtiene el estado del servicio.
     * Proporciona información básica sobre el funcionamiento del servicio.
     * 
     * @return ResponseEntity con estado del servicio
     */
    public ResponseEntity<?> obtenerStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Servicio de equipos funcionando correctamente");
        response.put("timestamp", System.currentTimeMillis());
        response.put("servicio", "EquipoService");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Crea un nuevo equipo y registra al creador como administrador.
     * 
     * @param request Datos del equipo a crear
     * @param usuarioCreador Usuario que crea el equipo
     * @return ResponseEntity con el resultado de la operación
     */
    public ResponseEntity<?> crearEquipo(CrearEquipoRequest request, Usuario usuarioCreador) {
        try {
            log.info("Creando equipo '{}' por usuario: {}", request.getNombre(), usuarioCreador.getEmail());
            
            // Validar que el usuario exista y esté activo
            if (usuarioCreador == null || usuarioCreador.getEliminado()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CrearEquipoResponse(null, null, "Usuario no válido o inactivo", false));
            }
            
            // Crear el equipo
            Equipo equipo = new Equipo();
            equipo.setIdEquipo(java.util.UUID.randomUUID().toString());
            equipo.setNombre(request.getNombre());
            equipo.setCreador(usuarioCreador);
            equipo.setFechaCreacion(java.time.LocalDateTime.now());
            equipo.setFechaModificacion(java.time.LocalDateTime.now());
            equipo.setEliminado(false);
            
            // Guardar el equipo
            equipo = equipoRepository.save(equipo);
            log.info("Equipo guardado con ID: {}", equipo.getIdEquipo());
            
            // Crear registro de miembro para el creador como administrador
            EquipoMiembro miembroCreador = new EquipoMiembro();
            miembroCreador.setIdMiembro(java.util.UUID.randomUUID().toString());
            miembroCreador.setEquipo(equipo);
            miembroCreador.setUsuario(usuarioCreador);
            miembroCreador.setRol(Rol.ADMINISTRADOR);
            miembroCreador.setFechaAsignacion(java.time.LocalDateTime.now());
            miembroCreador.setFechaModificacion(java.time.LocalDateTime.now());
            miembroCreador.setEliminado(false);
            
            // Guardar el miembro
            equipoMiembroRepository.save(miembroCreador);
            log.info("Creador {} registrado como administrador del equipo {}", 
                    usuarioCreador.getEmail(), equipo.getIdEquipo());
            
            // Retornar respuesta exitosa
            CrearEquipoResponse response = new CrearEquipoResponse(
                    equipo.getIdEquipo(),
                    equipo.getNombre(),
                    "Equipo creado exitosamente",
                    true
            );
            
            log.info("Equipo '{}' creado exitosamente por {}", equipo.getNombre(), usuarioCreador.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error al crear equipo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CrearEquipoResponse(null, null, "Error al crear el equipo: " + e.getMessage(), false));
        }
    }

    /**
     * Elimina un equipo verificando que el usuario sea administrador.
     * 
     * @param idEquipo ID del equipo a eliminar
     * @param usuarioSolicitante Usuario que solicita la eliminación
     * @return ResponseEntity con el resultado de la operación
     */
    public ResponseEntity<?> eliminarEquipo(String idEquipo, Usuario usuarioSolicitante) {
        try {
            log.info("Solicitud para eliminar equipo {} por usuario: {}", idEquipo, usuarioSolicitante.getEmail());
            
            // Validar que el ID del equipo no sea nulo o vacío
            if (idEquipo == null || idEquipo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EliminarEquipoResponse(null, null, "ID del equipo es obligatorio", false));
            }
            
            // Validar que el usuario exista y esté activo
            if (usuarioSolicitante == null || usuarioSolicitante.getEliminado()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new EliminarEquipoResponse(null, null, "Usuario no válido o inactivo", false));
            }
            
            // Verificar que el equipo existe
            Optional<Equipo> equipoOpt = equipoRepository.findById(idEquipo);
            if (equipoOpt.isEmpty()) {
                log.warn("Intento de eliminar equipo inexistente: {}", idEquipo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new EliminarEquipoResponse(idEquipo, null, "Equipo no encontrado", false));
            }
            
            Equipo equipo = equipoOpt.get();
            
            // Verificar que el usuario sea administrador del equipo
            boolean esAdministrador = equipoMiembroRepository.esAdministradorDeEquipo(
                    idEquipo, usuarioSolicitante.getIdUsuario());
            
            log.info("Verificación de administrador - Equipo: {}, Usuario: {}, EsAdministrador: {}", 
                    idEquipo, usuarioSolicitante.getIdUsuario(), esAdministrador);
            
            if (!esAdministrador) {
                log.warn("Usuario {} no es administrador del equipo {}", 
                        usuarioSolicitante.getIdUsuario(), idEquipo);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new EliminarEquipoResponse(idEquipo, equipo.getNombre(), 
                                "No tienes permisos de administrador para eliminar este equipo", false));
            }
            
            // Marcar equipo como eliminado (eliminación lógica)
            equipo.setEliminado(true);
            equipo.setFechaModificacion(java.time.LocalDateTime.now());
            equipoRepository.save(equipo);
            
            // Marcar todos los miembros del equipo como eliminados
            List<EquipoMiembro> miembros = equipoMiembroRepository.findMiembrosActivosByEquipoId(idEquipo);
            for (EquipoMiembro miembro : miembros) {
                miembro.setEliminado(true);
                miembro.setFechaModificacion(java.time.LocalDateTime.now());
            }
            equipoMiembroRepository.saveAll(miembros);
            
            log.info("Equipo {} eliminado exitosamente por {} ({} miembros afectados)", 
                    idEquipo, usuarioSolicitante.getEmail(), miembros.size());
            
            // Retornar respuesta exitosa
            EliminarEquipoResponse response = new EliminarEquipoResponse(
                    equipo.getIdEquipo(),
                    equipo.getNombre(),
                    "Equipo eliminado exitosamente",
                    true
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al eliminar equipo {}: {}", idEquipo, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EliminarEquipoResponse(idEquipo, null, 
                            "Error al eliminar el equipo: " + e.getMessage(), false));
        }
    }

    /**
     * Agrega un miembro a un equipo verificando que el solicitante sea administrador.
     * 
     * @param idEquipo ID del equipo
     * @param request Datos del miembro a agregar
     * @param usuarioSolicitante Usuario que solicita agregar el miembro
     * @return ResponseEntity con el resultado de la operación
     */
    public ResponseEntity<?> agregarMiembro(String idEquipo, AgregarMiembroRequest request, Usuario usuarioSolicitante) {
        try {
            log.info("Solicitud para agregar miembro {} al equipo {} por usuario: {}", 
                    request.getIdUsuario(), idEquipo, usuarioSolicitante.getEmail());
            
            // Validar que el ID del equipo no sea nulo o vacío
            if (idEquipo == null || idEquipo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AgregarMiembroResponse(null, null, null, null, null, null, 
                                "ID del equipo es obligatorio", false));
            }
            
            // Validar que el usuario solicitante exista y esté activo
            if (usuarioSolicitante == null || usuarioSolicitante.getEliminado()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AgregarMiembroResponse(null, null, null, null, null, null, 
                                "Usuario no válido o inactivo", false));
            }
            
            // Verificar que el equipo existe
            Optional<Equipo> equipoOpt = equipoRepository.findById(idEquipo);
            if (equipoOpt.isEmpty()) {
                log.warn("Intento de agregar miembro a equipo inexistente: {}", idEquipo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AgregarMiembroResponse(null, null, null, null, null, null, 
                                "Equipo no encontrado", false));
            }
            
            Equipo equipo = equipoOpt.get();
            
            // Verificar que el usuario solicitante sea administrador del equipo
            boolean esAdministrador = equipoMiembroRepository.esAdministradorDeEquipo(
                    idEquipo, usuarioSolicitante.getIdUsuario());
            
            log.info("Verificación de administrador - Equipo: {}, Usuario: {}, EsAdministrador: {}", 
                    idEquipo, usuarioSolicitante.getIdUsuario(), esAdministrador);
            
            if (!esAdministrador) {
                log.warn("Usuario {} no es administrador del equipo {}", 
                        usuarioSolicitante.getIdUsuario(), idEquipo);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AgregarMiembroResponse(null, null, null, null, null, equipo.getNombre(), 
                                "No tienes permisos de administrador para agregar miembros a este equipo", false));
            }
            
            // Buscar usuario a agregar por ID
            Usuario usuarioMiembro = usuarioRepository.findById(request.getIdUsuario()).orElse(null);
            if (usuarioMiembro == null) {
                log.warn("Usuario a agregar no encontrado: {}", request.getIdUsuario());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AgregarMiembroResponse(null, request.getIdUsuario(), null, null, null, equipo.getNombre(), 
                                "Usuario a agregar no encontrado", false));
            }
            
            // Verificar que el usuario a agregar no esté eliminado
            if (usuarioMiembro.getEliminado()) {
                log.warn("Usuario a agregar está inactivo: {}", request.getIdUsuario());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AgregarMiembroResponse(null, request.getIdUsuario(), null, null, null, equipo.getNombre(), 
                                "Usuario a agregar está inactivo", false));
            }
            
            // Verificar que el usuario no sea ya miembro del equipo
            boolean yaEsMiembro = equipoMiembroRepository.existsByEquipoAndUsuarioAndEliminadoFalse(
                    idEquipo, usuarioMiembro.getIdUsuario());
            
            if (yaEsMiembro) {
                log.warn("Usuario {} ya es miembro del equipo {}", 
                        request.getIdUsuario(), idEquipo);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new AgregarMiembroResponse(null, request.getIdUsuario(), null, null, null, equipo.getNombre(), 
                                "El usuario ya es miembro de este equipo", false));
            }
            
            // Solo se permite agregar miembros como COLABORADOR (no se puede agregar más administradores)
            Rol rolMiembro = Rol.COLABORADOR;
            
            // Crear nuevo miembro
            EquipoMiembro nuevoMiembro = new EquipoMiembro();
            nuevoMiembro.setIdMiembro(java.util.UUID.randomUUID().toString());
            nuevoMiembro.setEquipo(equipo);
            nuevoMiembro.setUsuario(usuarioMiembro);
            nuevoMiembro.setRol(rolMiembro);
            nuevoMiembro.setFechaAsignacion(java.time.LocalDateTime.now());
            nuevoMiembro.setFechaModificacion(java.time.LocalDateTime.now());
            nuevoMiembro.setEliminado(false);
            
            // Guardar el nuevo miembro
            equipoMiembroRepository.save(nuevoMiembro);
            
            // Construir nombre completo del miembro
            String nombreCompletoMiembro = usuarioMiembro.getUserName() + " " + usuarioMiembro.getUserLasname();
            String nombreCompletoSolicitante = usuarioSolicitante.getUserName() + " " + usuarioSolicitante.getUserLasname();
            
            log.info("Miembro {} agregado exitosamente al equipo {} con rol {}", 
                    request.getIdUsuario(), idEquipo, rolMiembro);
            
            // Enviar correo de notificación al miembro agregado (solo si no es el mismo usuario solicitante)
            try {
                // Validar que el usuario agregado no sea el mismo que el solicitante
                boolean esMismoUsuario = usuarioMiembro.getIdUsuario().equals(usuarioSolicitante.getIdUsuario());
                
                if (!esMismoUsuario) {
                    EquipoAsignadoEmailDTO emailDTO = new EquipoAsignadoEmailDTO(
                        nombreCompletoMiembro.trim(),
                        usuarioMiembro.getEmail(),
                        equipo.getNombre(),
                        nombreCompletoSolicitante.trim()
                    );
                    
                    // Nota: La entidad Equipo no tiene campo descripción actualmente
                    // Si en el futuro se agrega el campo descripción, se puede descomentar este código
                    // if (equipo.getDescripcion() != null && !equipo.getDescripcion().trim().isEmpty()) {
                    //     emailDTO.setDescripcionEquipo(equipo.getDescripcion());
                    // }
                    
                    boolean emailEnviado = notificacionService.enviarNotificacionEquipoAsignado(emailDTO);
                    if (emailEnviado) {
                        log.info("Correo de asignación a equipo enviado exitosamente al usuario: {}", usuarioMiembro.getEmail());
                    } else {
                        log.warn("No se pudo enviar el correo de asignación a equipo al usuario: {}", usuarioMiembro.getEmail());
                    }
                } else {
                    log.info("No se envía correo de notificación porque el usuario agregado ({}) es el mismo que el solicitante", 
                        usuarioMiembro.getEmail());
                }
            } catch (Exception e) {
                log.error("Error al enviar correo de asignación a equipo al usuario {}: {}", 
                    usuarioMiembro.getEmail(), e.getMessage(), e);
                // No lanzamos la excepción para no interrumpir la asignación
            }
            
            // Retornar respuesta exitosa
            AgregarMiembroResponse response = new AgregarMiembroResponse(
                    nuevoMiembro.getIdMiembro(),
                    usuarioMiembro.getIdUsuario(),
                    usuarioMiembro.getEmail(),
                    nombreCompletoMiembro,
                    rolMiembro.name(),
                    equipo.getNombre(),
                    "Miembro agregado exitosamente",
                    true
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error al agregar miembro al equipo {}: {}", idEquipo, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AgregarMiembroResponse(null, request.getIdUsuario(), null, null, null, null, 
                            "Error al agregar el miembro: " + e.getMessage(), false));
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
}
