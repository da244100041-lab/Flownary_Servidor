package com.codepro.flownary_servidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de verificación de contactos.
 * Contiene la lista de contactos encontrados y estadísticas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactosResponse {
    private List<ContactoEncontrado> encontrados;
    private int totalEncontrados;
    private int totalSolicitados;
    private String message;
    
    public ContactosResponse(List<ContactoEncontrado> encontrados, int totalSolicitados) {
        this.encontrados = encontrados;
        this.totalEncontrados = encontrados.size();
        this.totalSolicitados = totalSolicitados;
        this.message = "Se encontraron " + totalEncontrados + " contactos de " + totalSolicitados + " solicitados";
    }
}
