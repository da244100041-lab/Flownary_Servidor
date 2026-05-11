package com.codepro.flownary_servidor.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO para solicitud de verificación de contactos.
 * Contiene la lista de números de teléfono a verificar.
 */
@Data
public class ContactosRequest {
    private List<String> telefonos;
}
