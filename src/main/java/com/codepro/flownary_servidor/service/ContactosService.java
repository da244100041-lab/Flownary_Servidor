package com.codepro.flownary_servidor.service;

import com.codepro.flownary_servidor.dto.ContactoEncontrado;
import com.codepro.flownary_servidor.dto.ContactosRequest;
import com.codepro.flownary_servidor.dto.ContactosResponse;
import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio para la verificación de contactos telefónicos.
 * Busca usuarios registrados por sus números de teléfono.
 */
@Service
@Slf4j
public class ContactosService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Patrón para validar que el teléfono comience con +503 y tenga 8 dígitos adicionales
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+503\\d{8}$");

    /**
     * Verifica qué números de teléfono están registrados en el sistema.
     * 
     * @param request Solicitud con lista de números de teléfono
     * @return Respuesta con los contactos encontrados
     */
    public ContactosResponse verificarContactos(ContactosRequest request) {
        // Validar que la lista no sea nula
        if (request == null || request.getTelefonos() == null) {
            throw new IllegalArgumentException("La lista de teléfonos es obligatoria");
        }

        List<String> telefonosSolicitados = request.getTelefonos();
        log.info("Verificando {} números de teléfono", telefonosSolicitados.size());

        // Limpiar y normalizar los números de teléfono
        List<String> telefonosLimpios = new ArrayList<>();
        for (String telefono : telefonosSolicitados) {
            String telefonoLimpio = limpiarTelefono(telefono);
            if (telefonoLimpio != null && validarTelefono(telefonoLimpio)) {
                telefonosLimpios.add(telefonoLimpio);
            } else {
                log.warn("Número de teléfono inválido ignorado: {}", telefono);
            }
        }

        log.info("Se procesarán {} números válidos de {} solicitados", 
                telefonosLimpios.size(), telefonosSolicitados.size());

        // Buscar usuarios por los números de teléfono
        List<Usuario> usuariosEncontrados = usuarioRepository.findByTelefonoInAndEliminadoFalse(telefonosLimpios);
        log.info("Se encontraron {} usuarios en la base de datos", usuariosEncontrados.size());

        // Convertir usuarios a DTOs de respuesta
        List<ContactoEncontrado> contactosEncontrados = new ArrayList<>();
        for (Usuario usuario : usuariosEncontrados) {
            ContactoEncontrado contacto = new ContactoEncontrado(
                usuario.getIdUsuario(),
                usuario.getUserName(),
                usuario.getUserLasname(),
                usuario.getEmail(),
                usuario.getTelefono()
            );
            contactosEncontrados.add(contacto);
        }

        // Crear y retornar respuesta
        return new ContactosResponse(contactosEncontrados, telefonosSolicitados.size());
    }

    /**
     * Limpia y normaliza un número de teléfono.
     * 
     * @param telefono Teléfono a limpiar
     * @return Teléfono limpio o null si es inválido
     */
    private String limpiarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
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
            } else {
                return null; // No hay suficientes dígitos
            }
        }

        return telefonoLimpio;
    }

    /**
     * Valida el formato de un número de teléfono.
     * 
     * @param telefono Teléfono a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return TELEFONO_PATTERN.matcher(telefono).matches();
    }
}
