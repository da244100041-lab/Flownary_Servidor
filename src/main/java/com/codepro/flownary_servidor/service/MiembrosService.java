package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.dto.MiembroEquipoDTO;
import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.EquipoMiembro;
import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.repository.EquipoMiembroRepository;
import com.codepro.flownary_servidor.repository.EquipoRepository;
import com.codepro.flownary_servidor.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * Servicio para consultar miembros de equipos.
 * Proporciona información detallada de los miembros activos de un equipo.
 */
@Service
@Slf4j
public class MiembrosService {

    @Autowired
    private EquipoMiembroRepository equipoMiembroRepository;
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene los miembros activos de un equipo con información detallada.
     * Incluye nombre completo concatenado, email y rol del usuario.
     * 
     * @param idEquipo ID del equipo a consultar
     * @return ResponseEntity con la lista de miembros o error
     */
    public ResponseEntity<?> obtenerMiembrosActivos(String idEquipo) {
        try {
            // Validar que el ID del equipo no sea nulo o vacío
            if (idEquipo == null || idEquipo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("El ID del equipo es obligatorio"));
            }

            log.info("Consultando miembros activos del equipo: {}", idEquipo);

            // Verificar que el equipo existe
            Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
            if (equipo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearRespuestaError("Equipo no encontrado"));
            }

            // Validar que el equipo existe y obtener el email del usuario autenticado
            // Usar SecurityContextHolder para obtener el email del usuario autenticado
            String emailUsuarioAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            if (emailUsuarioAutenticado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario no autenticado"));
            }

            // Buscar usuario autenticado
            Usuario usuarioAutenticado = usuarioRepository.findByEmailAndEliminadoFalse(emailUsuarioAutenticado)
                    .orElse(null);
            if (usuarioAutenticado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearRespuestaError("Usuario autenticado no encontrado"));
            }

            // Verificar si el usuario autenticado es miembro del equipo
            try {
                boolean esMiembro = equipoMiembroRepository.existsByEquipoAndUsuarioAndEliminadoFalse(
                        idEquipo, usuarioAutenticado.getIdUsuario());
                
                log.info("Verificación de membresía - Equipo: {}, Usuario: {}, EsMiembro: {}", 
                        idEquipo, usuarioAutenticado.getIdUsuario(), esMiembro);
                
                if (!esMiembro) {
                    log.warn("Usuario {} no es miembro del equipo {}", 
                            usuarioAutenticado.getIdUsuario(), idEquipo);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(crearRespuestaError("No tienes permiso para consultar los miembros de este equipo"));
                }
            } catch (Exception e) {
                log.error("Error al verificar membresía del usuario {} en equipo {}: {}", 
                        usuarioAutenticado.getIdUsuario(), idEquipo, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(crearRespuestaError("Error al verificar permisos de acceso"));
            }

            // Convertir a DTOs con información concatenada
            List<MiembroEquipoDTO> miembrosDTO = new ArrayList<>();
            
            // Obtener miembros activos del equipo
            List<EquipoMiembro> miembros;
            try {
                miembros = equipoMiembroRepository.findMiembrosActivosByEquipoId(idEquipo);
                log.info("Consulta de miembros - Equipo: {}, Miembros encontrados: {}", idEquipo, miembros.size());
                
                if (miembros.isEmpty()) {
                    log.warn("No se encontraron miembros activos para el equipo {}", idEquipo);
                    return ResponseEntity.ok(miembrosDTO);
                }
            } catch (Exception e) {
                log.error("Error al consultar miembros del equipo {}: {}", idEquipo, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(crearRespuestaError("Error al consultar los miembros del equipo"));
            }
            
            for (EquipoMiembro miembro : miembros) {
                Usuario usuario = miembro.getUsuario();
                if (usuario != null) {
                    // Concatenar nombre completo
                    String nombreCompleto = concatenarNombreCompleto(usuario);
                    
                    // Crear DTO con la información requerida
                    MiembroEquipoDTO miembroDTO = new MiembroEquipoDTO(
                        usuario.getIdUsuario(),
                        nombreCompleto,
                        usuario.getEmail(),
                        miembro.getRol().toString()
                    );
                    
                    miembrosDTO.add(miembroDTO);
                    
                    log.debug("Miembro agregado: {} ({}) - Rol: {}", 
                            nombreCompleto, usuario.getEmail(), miembro.getRol());
                }
            }

            // Crear respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("miembros", miembrosDTO);
            response.put("totalMiembros", miembrosDTO.size());
            response.put("idEquipo", idEquipo);
            response.put("nombreEquipo", equipo.getNombre());
            response.put("message", "Se encontraron " + miembrosDTO.size() + " miembros activos");

            log.info("Retornando {} miembros para el equipo {}: {}", 
                    miembrosDTO.size(), idEquipo, equipo.getNombre());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener miembros del equipo {}: {}", idEquipo, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error interno del servidor al obtener los miembros del equipo"));
        }
    }

    /**
     * Concatena el nombre completo del usuario.
     * Maneja casos donde alguno de los campos sea nulo.
     * 
     * @param usuario Usuario del cual concatenar el nombre
     * @return Nombre completo concatenado
     */
    private String concatenarNombreCompleto(Usuario usuario) {
        StringBuilder nombreCompleto = new StringBuilder();
        
        // Agregar nombre si no es nulo o vacío
        if (usuario.getUserName() != null && !usuario.getUserName().trim().isEmpty()) {
            nombreCompleto.append(usuario.getUserName().trim());
        }
        
        // Agregar apellido si no es nulo o vacío
        if (usuario.getUserLasname() != null && !usuario.getUserLasname().trim().isEmpty()) {
            if (nombreCompleto.length() > 0) {
                nombreCompleto.append(" ");
            }
            nombreCompleto.append(usuario.getUserLasname().trim());
        }
        
        // Si ambos son nulos o vacíos, usar el email como fallback
        if (nombreCompleto.length() == 0) {
            nombreCompleto.append(usuario.getEmail());
        }
        
        return nombreCompleto.toString();
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
