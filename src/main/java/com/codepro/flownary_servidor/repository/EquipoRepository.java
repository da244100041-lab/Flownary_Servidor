package com.codepro.flownary_servidor.repository;

import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de entidades Equipo.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre la tabla de equipos del sistema, incluyendo relaciones con usuarios.
 */
@Repository
public interface EquipoRepository extends JpaRepository<Equipo, String> {
    
    /**
     * Obtiene todos los equipos activos del sistema.
     * Excluye los equipos marcados como eliminados.
     * @return Lista de equipos activos.
     */
    List<Equipo> findByEliminadoFalse();
    
    /**
     * Obtiene todos los equipos activos creados por un usuario específico.
     * @param creador Usuario que creó los equipos.
     * @return Lista de equipos activos creados por el usuario.
     */
    List<Equipo> findByCreadorAndEliminadoFalse(Usuario creador);
    
    /**
     * Obtiene todos los equipos activos en los que un usuario es miembro.
     * Busca equipos donde el usuario tiene membresía activa (no eliminada).
     * @param usuario Usuario miembro de los equipos.
     * @return Lista de equipos donde el usuario es miembro activo.
     */
    @Query("SELECT e FROM Equipo e JOIN e.miembros m WHERE m.usuario = :usuario AND m.eliminado = false AND e.eliminado = false")
    List<Equipo> findEquiposByMiembro(@Param("usuario") Usuario usuario);
    
    /**
     * Busca equipos activos por nombre parcial.
     * Realiza búsqueda parcial en el campo nombre del equipo.
     * @param nombre Texto a buscar en el nombre del equipo.
     * @return Lista de equipos que coinciden con el criterio de búsqueda.
     */
    @Query("SELECT e FROM Equipo e WHERE e.eliminado = false AND (e.nombre LIKE %:nombre%)")
    List<Equipo> findByNombreContainingAndEliminadoFalse(@Param("nombre") String nombre);
    
    /**
     * Busca equipos activos de un creador específico por nombre parcial.
     * Combina filtro por creador y búsqueda parcial en nombre.
     * @param creador Usuario creador de los equipos.
     * @param nombre Texto a buscar en el nombre del equipo.
     * @return Lista de equipos del creador que coinciden con el criterio.
     */
    @Query("SELECT e FROM Equipo e WHERE e.creador = :creador AND e.eliminado = false AND (e.nombre LIKE %:nombre%)")
    List<Equipo> findByCreadorAndNombreContainingAndEliminadoFalse(@Param("creador") Usuario creador, @Param("nombre") String nombre);
    
    /**
     * Cuenta los equipos activos creados por un usuario específico.
     * @param creador Usuario creador de los equipos.
     * @return Número de equipos activos creados por el usuario.
     */
    @Query("SELECT COUNT(e) FROM Equipo e WHERE e.creador = :creador AND e.eliminado = false")
    long countByCreadorAndEliminadoFalse(@Param("creador") Usuario creador);
    
    /**
     * Verifica si existe un equipo activo con el nombre especificado.
     * @param nombre Nombre del equipo a verificar.
     * @return true si existe un equipo activo con ese nombre, false en caso contrario.
     */
    boolean existsByNombreAndEliminadoFalse(String nombre);
    
    /**
     * Obtiene todos los equipos activos donde un usuario es miembro o creador.
     * Incluye equipos creados por el usuario y equipos donde es miembro activo.
     * @param usuario Usuario para consultar sus equipos.
     * @return Lista de equipos activos del usuario.
     */
    @Query("SELECT DISTINCT e FROM Equipo e " +
           "LEFT JOIN e.miembros m ON m.usuario.idUsuario = :usuarioId AND m.eliminado = false " +
           "WHERE (e.creador.idUsuario = :usuarioId OR m.usuario IS NOT NULL) " +
           "AND e.eliminado = false")
    List<Equipo> findEquiposByUsuario(@Param("usuarioId") String usuarioId);
}
