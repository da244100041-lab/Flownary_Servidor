package com.codepro.flownary_servidor.repository;

import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.MensajeChat;
import com.codepro.flownary_servidor.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de entidades MensajeChat.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre los mensajes del chat del sistema, incluyendo filtrado por fecha y texto.
 */
@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, String> {
    
    /**
     * Obtiene todos los mensajes activos de un equipo ordenados cronológicamente.
     * @param equipo Equipo del cual se obtienen los mensajes.
     * @return Lista de mensajes activos ordenados por fecha de envío ascendente.
     */
    List<MensajeChat> findByEquipoAndEliminadoFalseOrderByFechaEnvioAsc(Equipo equipo);
    
    /**
     * Obtiene los mensajes activos de un usuario en un equipo ordenados por fecha descendente.
     * @param equipo Equipo donde se enviaron los mensajes.
     * @param usuario Usuario que envió los mensajes.
     * @return Lista de mensajes del usuario ordenados por fecha de envío descendente.
     */
    List<MensajeChat> findByEquipoAndUsuarioAndEliminadoFalseOrderByFechaEnvioDesc(Equipo equipo, Usuario usuario);
    
    /**
     * Obtiene mensajes activos de un equipo enviados después de una fecha específica.
     * @param equipo Equipo del cual se obtienen los mensajes.
     * @param fechaInicio Fecha a partir de la cual buscar mensajes.
     * @return Lista de mensajes enviados después de la fecha especificada.
     */
    @Query("SELECT m FROM MensajeChat m WHERE m.equipo = :equipo AND m.eliminado = false AND m.fechaEnvio >= :fechaInicio ORDER BY m.fechaEnvio ASC")
    List<MensajeChat> findByEquipoAndFechaEnvioAfterAndEliminadoFalseOrderByFechaEnvioAsc(@Param("equipo") Equipo equipo, @Param("fechaInicio") LocalDateTime fechaInicio);
    
    /**
     * Obtiene mensajes activos de un equipo dentro de un rango de fechas.
     * @param equipo Equipo del cual se obtienen los mensajes.
     * @param fechaInicio Fecha inicial del rango.
     * @param fechaFin Fecha final del rango.
     * @return Lista de mensajes dentro del rango de fechas especificado.
     */
    @Query("SELECT m FROM MensajeChat m WHERE m.equipo = :equipo AND m.eliminado = false AND m.fechaEnvio BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaEnvio ASC")
    List<MensajeChat> findByEquipoAndFechaEnvioBetweenAndEliminadoFalseOrderByFechaEnvioAsc(@Param("equipo") Equipo equipo, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Busca mensajes activos de un equipo que contienen un texto específico.
     * @param equipo Equipo donde buscar los mensajes.
     * @param texto Texto a buscar en el contenido del mensaje.
     * @return Lista de mensajes que contienen el texto especificado.
     */
    @Query("SELECT m FROM MensajeChat m WHERE m.equipo = :equipo AND m.eliminado = false AND (m.mensaje LIKE %:texto%) ORDER BY m.fechaEnvio DESC")
    List<MensajeChat> findByEquipoAndMensajeContainingAndEliminadoFalseOrderByFechaEnvioDesc(@Param("equipo") Equipo equipo, @Param("texto") String texto);
    
    /**
     * Cuenta todos los mensajes activos de un equipo específico.
     * @param equipo Equipo del cual contar los mensajes.
     * @return Número total de mensajes activos en el equipo.
     */
    @Query("SELECT COUNT(m) FROM MensajeChat m WHERE m.equipo = :equipo AND m.eliminado = false")
    long countByEquipoAndEliminadoFalse(@Param("equipo") Equipo equipo);
    
    /**
     * Cuenta los mensajes activos de un usuario específico en un equipo.
     * @param equipo Equipo donde se enviaron los mensajes.
     * @param usuario Usuario que envió los mensajes.
     * @return Número de mensajes del usuario en el equipo.
     */
    @Query("SELECT COUNT(m) FROM MensajeChat m WHERE m.equipo = :equipo AND m.usuario = :usuario AND m.eliminado = false")
    long countByEquipoAndUsuarioAndEliminadoFalse(@Param("equipo") Equipo equipo, @Param("usuario") Usuario usuario);
    
    /**
     * Obtiene los 50 mensajes más recientes de un equipo.
     * @param equipo Equipo del cual se obtienen los mensajes.
     * @return Lista de los 50 mensajes más recientes ordenados por fecha descendente.
     */
    @Query("SELECT m FROM MensajeChat m WHERE m.equipo = :equipo AND m.eliminado = false ORDER BY m.fechaEnvio DESC")
    List<MensajeChat> findTop50ByEquipoAndEliminadoFalseOrderByFechaEnvioDesc(@Param("equipo") Equipo equipo);
}
