package com.codepro.flownary_servidor.repository;

import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.EstadoKanban;
import com.codepro.flownary_servidor.entity.TareaPrincipal;
import com.codepro.flownary_servidor.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de entidades TareaPrincipal.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre las tareas principales del sistema, incluyendo filtrado por estado Kanban.
 */
@Repository
public interface TareaPrincipalRepository extends JpaRepository<TareaPrincipal, String> {
    
    /**
     * Obtiene todas las tareas activas de un equipo específico.
     * @param equipo Equipo del cual se obtienen las tareas.
     * @return Lista de tareas activas del equipo.
     */
    List<TareaPrincipal> findByEquipoAndEliminadoFalse(Equipo equipo);
    
    /**
     * Obtiene todas las tareas activas creadas por un usuario específico.
     * @param creador Usuario que creó las tareas.
     * @return Lista de tareas activas creadas por el usuario.
     */
    List<TareaPrincipal> findByCreadorAndEliminadoFalse(Usuario creador);
    
    /**
     * Obtiene las tareas activas de un equipo filtradas por estado Kanban.
     * @param equipo Equipo del cual se obtienen las tareas.
     * @param estadoKanban Estado Kanban a filtrar (PENDIENTE, EN_PROCESO, COMPLETADO).
     * @return Lista de tareas del equipo con el estado especificado.
     */
    List<TareaPrincipal> findByEquipoAndEstadoKanbanAndEliminadoFalse(Equipo equipo, EstadoKanban estadoKanban);
    
    /**
     * Obtiene las tareas activas de un equipo creadas por un usuario específico.
     * @param equipo Equipo del cual se obtienen las tareas.
     * @param creador Usuario que creó las tareas.
     * @return Lista de tareas del equipo creadas por el usuario.
     */
    List<TareaPrincipal> findByEquipoAndCreadorAndEliminadoFalse(Equipo equipo, Usuario creador);
    
    /**
     * Busca tareas activas por título o descripción (búsqueda global).
     * Realiza búsqueda parcial en ambos campos.
     * @param titulo Texto a buscar en el título de la tarea.
     * @param descripcion Texto a buscar en la descripción de la tarea.
     * @return Lista de tareas que coinciden con el criterio de búsqueda.
     */
    @Query("SELECT t FROM TareaPrincipal t WHERE t.eliminado = false AND (t.titulo LIKE %:titulo% OR t.descripcion LIKE %:descripcion%)")
    List<TareaPrincipal> findByTituloOrDescripcionContainingAndEliminadoFalse(@Param("titulo") String titulo, @Param("descripcion") String descripcion);
    
    /**
     * Busca tareas activas de un equipo por título o descripción.
     * Combina filtro por equipo y búsqueda parcial en título/descripción.
     * @param equipo Equipo donde buscar las tareas.
     * @param titulo Texto a buscar en el título de la tarea.
     * @param descripcion Texto a buscar en la descripción de la tarea.
     * @return Lista de tareas del equipo que coinciden con el criterio.
     */
    @Query("SELECT t FROM TareaPrincipal t WHERE t.equipo = :equipo AND t.eliminado = false AND (t.titulo LIKE %:titulo% OR t.descripcion LIKE %:descripcion%)")
    List<TareaPrincipal> findByEquipoAndTituloOrDescripcionContainingAndEliminadoFalse(@Param("equipo") Equipo equipo, @Param("titulo") String titulo, @Param("descripcion") String descripcion);
    
    /**
     * Obtiene las tareas activas de un equipo por estado Kanban ordenadas por fecha de creación descendente.
     * @param equipo Equipo del cual se obtienen las tareas.
     * @param estadoKanban Estado Kanban a filtrar.
     * @return Lista de tareas ordenadas por fecha de creación (más recientes primero).
     */
    @Query("SELECT t FROM TareaPrincipal t WHERE t.equipo = :equipo AND t.estadoKanban = :estadoKanban AND t.eliminado = false ORDER BY t.fechaCreacion DESC")
    List<TareaPrincipal> findByEquipoAndEstadoKanbanAndEliminadoFalseOrderByFechaCreacionDesc(@Param("equipo") Equipo equipo, @Param("estadoKanban") EstadoKanban estadoKanban);
    
    /**
     * Cuenta las tareas activas de un equipo por estado Kanban específico.
     * @param equipo Equipo del cual contar las tareas.
     * @param estadoKanban Estado Kanban a filtrar.
     * @return Número de tareas con el estado especificado en el equipo.
     */
    @Query("SELECT COUNT(t) FROM TareaPrincipal t WHERE t.equipo = :equipo AND t.estadoKanban = :estadoKanban AND t.eliminado = false")
    long countByEquipoAndEstadoKanbanAndEliminadoFalse(@Param("equipo") Equipo equipo, @Param("estadoKanban") EstadoKanban estadoKanban);
    
    /**
     * Cuenta todas las tareas activas de un equipo específico.
     * @param equipo Equipo del cual contar las tareas.
     * @return Número total de tareas activas en el equipo.
     */
    @Query("SELECT COUNT(t) FROM TareaPrincipal t WHERE t.equipo = :equipo AND t.eliminado = false")
    long countByEquipoAndEliminadoFalse(@Param("equipo") Equipo equipo);
    
    /**
     * Cuenta todas las tareas activas creadas por un usuario específico.
     * @param creador Usuario que creó las tareas.
     * @return Número total de tareas activas creadas por el usuario.
     */
    @Query("SELECT COUNT(t) FROM TareaPrincipal t WHERE t.creador = :creador AND t.eliminado = false")
    long countByCreadorAndEliminadoFalse(@Param("creador") Usuario creador);
}
