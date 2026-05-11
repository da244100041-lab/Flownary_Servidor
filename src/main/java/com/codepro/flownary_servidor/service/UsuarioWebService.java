package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * Servicio específico para la página web de Flownary.
 * Maneja la lógica de negocio para el registro y gestión de usuarios desde la interfaz web.
 * Se comunica directamente con UsuarioRepository.
 */
@Service
@Transactional
@Slf4j
public class UsuarioWebService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SecurityService securityService;

    // Patrón para validar que el teléfono comience con +503 y tenga 8 dígitos adicionales
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+503\\d{8}$");
    
    // Patrón para validar email básico
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Registra un nuevo usuario desde la página web.
     * Realiza validaciones completas antes de guardar en la base de datos.
     * 
     * @param usuario Usuario a registrar
     * @return Usuario guardado
     * @throws IllegalArgumentException si hay errores de validación
     */
    public Usuario registrarUsuarioWeb(Usuario usuario) {
        log.info("Iniciando registro de usuario web: {}", usuario.getEmail());
        
        // Validar campos obligatorios
        validarCamposObligatorios(usuario);
        
        // Validar formato del email
        if (!validarEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }
        
        // Validar formato del teléfono
        log.info("Validando teléfono: {}", usuario.getTelefono());
        if (!validarTelefono(usuario.getTelefono())) {
            log.error("Teléfono inválido: {}", usuario.getTelefono());
            throw new IllegalArgumentException("El teléfono debe comenzar con +503 y tener 8 dígitos adicionales. Formato recibido: " + usuario.getTelefono());
        }
        log.info("Teléfono válido: {}", usuario.getTelefono());
        
        // Verificar que el email no exista
        if (usuarioRepository.existsByEmailAndEliminadoFalse(usuario.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado en el sistema");
        }
        
        // Limpiar y formatear el teléfono
        usuario.setTelefono(limpiarTelefono(usuario.getTelefono()));
        
        // Hashear la contraseña usando el servicio de seguridad
        String passwordPlano = usuario.getPassword();
        String passwordHasheado = securityService.hashPassword(passwordPlano);
        usuario.setPassword(passwordHasheado);
        log.info("Contraseña hasheada para usuario: {}", usuario.getEmail());
        
        // Guardar el usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario web registrado exitosamente: {}", usuarioGuardado.getEmail());
        
        return usuarioGuardado;
    }

    /**
     * Valida que todos los campos obligatorios del usuario estén presentes.
     * 
     * @param usuario Usuario a validar
     * @throws IllegalArgumentException si algún campo obligatorio falta
     */
    private void validarCamposObligatorios(Usuario usuario) {
        if (usuario.getUserName() == null || usuario.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        if (usuario.getUserLasname() == null || usuario.getUserLasname().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        
        if (usuario.getTelefono() == null || usuario.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono es obligatorio");
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
    }

    /**
     * Valida el formato del correo electrónico.
     * 
     * @param email Correo electrónico a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida el formato del teléfono.
     * Debe comenzar con +503 y tener exactamente 8 dígitos adicionales.
     * 
     * @param telefono Teléfono a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return TELEFONO_PATTERN.matcher(limpiarTelefono(telefono)).matches();
    }

    /**
     * Limpia y formatea el número de teléfono.
     * Elimina espacios, guiones, paréntesis y otros caracteres no deseados.
     * 
     * @param telefono Teléfono a limpiar
     * @return Teléfono limpio y formateado
     */
    private String limpiarTelefono(String telefono) {
        log.info("Limpiando teléfono: {}", telefono);
        
        if (telefono == null) {
            log.warn("Teléfono es nulo");
            return null;
        }
        
        // Eliminar todos los caracteres excepto +, números y espacios
        String telefonoLimpio = telefono.replaceAll("[^+0-9\\s]", "");
        log.info("Teléfono después de limpiar caracteres: {}", telefonoLimpio);
        
        // Eliminar espacios
        telefonoLimpio = telefonoLimpio.replaceAll("\\s+", "");
        log.info("Teléfono después de eliminar espacios: {}", telefonoLimpio);
        
        // Si no comienza con +503, intentar corregirlo
        if (!telefonoLimpio.startsWith("+503")) {
            log.info("Teléfono no comienza con +503, intentando corregir...");
            // Eliminar cualquier prefijo y agregar +503
            String numeros = telefonoLimpio.replaceAll("[^0-9]", "");
            log.info("Números extraídos: {}", numeros);
            
            if (numeros.length() >= 8) {
                // Tomar los últimos 8 dígitos
                numeros = numeros.substring(numeros.length() - 8);
                telefonoLimpio = "+503" + numeros;
                log.info("Teléfono corregido a: {}", telefonoLimpio);
            } else {
                log.warn("No hay suficientes dígitos para formar un número válido: {}", numeros);
            }
        }
        
        log.info("Teléfono final limpio: {}", telefonoLimpio);
        return telefonoLimpio;
    }

    /**
     * Verifica si un email ya está registrado en el sistema.
     * 
     * @param email Email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmailAndEliminadoFalse(email);
    }

    /**
     * Busca un usuario activo por su email.
     * 
     * @param email Email del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndEliminadoFalse(email).orElse(null);
    }

    /**
     * Obtiene todos los usuarios activos del sistema.
     * 
     * @return Lista de usuarios activos
     */
    public java.util.List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findByEliminadoFalse();
    }

    /**
     * Busca usuarios activos por nombre o apellido.
     * 
     * @param nombre Texto a buscar en nombre o apellido
     * @return Lista de usuarios que coinciden con el criterio
     */
    public java.util.List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingAndEliminadoFalse(nombre);
    }
}
