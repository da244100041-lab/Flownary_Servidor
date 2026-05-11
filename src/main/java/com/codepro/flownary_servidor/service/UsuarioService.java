package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.dto.LoginResponse;
import com.codepro.flownary_servidor.dto.BienvenidaEmailDTO;
import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.repository.UsuarioRepository;
import com.codepro.flownary_servidor.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * Servicio para la gestión de usuarios.
 * Proporciona lógica de negocio para el registro y validación de usuarios.
 */
@Service
@Transactional
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private NotificacionService notificacionService;

    // Patrón para validar que el teléfono comience con +503 y tenga 8 dígitos adicionales
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+503\\d{8}$");
    
    // Patrón para validar email básico
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Registra un nuevo usuario en el sistema.
     * Realiza validaciones de campos obligatorios, email y teléfono antes de guardar.
     * 
     * @param usuario Usuario a registrar
     * @return Usuario guardado
     * @throws IllegalArgumentException si hay errores de validación
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Validar campos obligatorios
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
        
        // Validar formato del email
        if (!validarEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }
        
        // Validar formato del teléfono
        if (!validarTelefono(usuario.getTelefono())) {
            throw new IllegalArgumentException("El teléfono debe comenzar con +503 y tener 8 dígitos adicionales");
        }
        
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
        
        // Guardar el usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Usuario registrado exitosamente (notificaciones por WebSocket se manejarán en la app móvil)
        log.info("Usuario registrado exitosamente: {}", usuarioGuardado.getEmail());
        
        return usuarioGuardado;
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
        if (telefono == null) {
            return null;
        }
        
        // Eliminar todos los caracteres excepto +, números y espacios
        String telefonoLimpio = telefono.replaceAll("[^+0-9\\s]", "");
        
        // Eliminar espacios
        telefonoLimpio = telefonoLimpio.replaceAll("\\s+", "");
        
        // Si no comienza con +503, intentar corregirlo
        if (!telefonoLimpio.startsWith("+503")) {
            // Eliminar cualquier prefijo y agregar +503
            String numeros = telefonoLimpio.replaceAll("[^0-9]", "");
            if (numeros.length() >= 8) {
                // Tomar los últimos 8 dígitos
                numeros = numeros.substring(numeros.length() - 8);
                telefonoLimpio = "+503" + numeros;
            }
        }
        
        return telefonoLimpio;
    }

    /**
     * Verifica si un email ya está registrado.
     * 
     * @param email Email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmailAndEliminadoFalse(email);
    }

    /**
     * Busca un usuario por su email.
     * 
     * @param email Email del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndEliminadoFalse(email).orElse(null);
    }

    /**
     * Autentica un usuario con email y contraseña.
     * 
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return LoginResponse con token y datos del usuario
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    public LoginResponse autenticarUsuario(String email, String password) {
        // Validar que los datos no sean nulos o vacíos
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        
        // Buscar usuario por email
        Usuario usuario = buscarPorEmail(email.trim());
        if (usuario == null) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        // Verificar la contraseña
        boolean passwordValida = securityService.verifyPassword(password, usuario.getPassword());
        if (!passwordValida) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        // Generar token JWT
        String token = securityService.generateSessionToken(email);
        
        // Crear y retornar respuesta
        LoginResponse response = new LoginResponse(
            token,
            usuario.getEmail(),
            usuario.getUserName(),
            usuario.getUserLasname()
        );
        
        // Imprimir token en consola para pruebas
        System.out.println("=================================================");
        System.out.println("TOKEN_ACTIVO_PARA_PRUEBAS - COPIAR ESTE TOKEN:");
        System.out.println("=================================================");
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Token: " + token);
        System.out.println("=================================================");
        System.out.println("PARA USAR EN POSTMAN/INSOMNIA:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("TOKEN COMPLETO PARA COPIAR Y PEGAR:");
        System.out.println("Bearer " + token);
        System.out.println("=================================================");
        
        return response;
    }
}
