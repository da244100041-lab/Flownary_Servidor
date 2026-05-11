package com.codepro.flownary_servidor.repository;

import com.codepro.flownary_servidor.entity.EstadoKanban;
import com.codepro.flownary_servidor.entity.Subtarea;
import com.codepro.flownary_servidor.entity.TareaPrincipal;
import com.codepro.flownary_servidor.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de entidades Subtarea.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre las subtareas del sistema, incluyendo ordenamiento y estado Kanban.
 */
@Repository
public interface SubtareaRepository extends JpaRepository<Subtarea, String> {
    
    /**
     * Obtiene todas las subtareas activas de una tarea principal ordenadas por orden.
     * @param tarea Tarea principal de la cual se obtienen las subtareas.
     * @return Lista de subtareas activas ordenadas por orden ascendente.
     */
    List<Subtarea> findByTareaAndEliminadoFalseOrderByOrdenAsc(TareaPrincipal tarea);
    
    /**
     * Obtiene todas las subtareas activas creadas por un usuario específico.
     * @param creador Usuario que creó las subtareas.
     * @return Lista de subtareas activas creadas por el usuario.
     */
    List<Subtarea> findByCreadorAndEliminadoFalse(Usuario creador);
    
    /**
     * Obtiene las subtareas activas de una tarea filtradas por estado Kanban y ordenadas.
     * @param tarea Tarea principal de la cual se obtienen las subtareas.
     * @param estadoKanban Estado Kanban a filtrar (PENDIENTE, EN_PROCESO, COMPLETADO).
     * @return Lista de subtareas con el estado especificado ordenadas por orden.
     */
    List<Subtarea> findByTareaAndEstadoKanbanAndEliminadoFalseOrderByOrdenAsc(TareaPrincipal tarea, EstadoKanban estadoKanban);
    
    /**
     * Obtiene las subtareas activas de una tarea creadas por un usuario específico.
     * @param tarea Tarea principal de la cual se obtienen las subtareas.
     * @param creador Usuario que creó las subtareas.
     * @return Lista de subtareas del usuario ordenadas por orden.
     */
    List<Subtarea> findByTareaAndCreadorAndEliminadoFalseOrderByOrdenAsc(TareaPrincipal tarea, Usuario creador);
    
    /**
     * Busca subtareas activas por título (búsqueda global).
     * Realiza búsqueda parcial en el campo título.
     * @param titulo Texto a buscar en el título de la subtarea.
     * @return Lista de subtareas que coinciden con el criterio de búsqueda.
     */
    @Query("SELECT s FROM Subtarea s WHERE s.eliminado = false AND (s.titulo LIKE %:titulo%)")
    List<Subtarea> findByTituloContainingAndEliminadoFalse(@Param("titulo") String titulo);
    
    /**
     * Busca subtareas activas de una tarea por título parcial.
     * Combina filtro por tarea y búsqueda parcial en título.
     * @param tarea Tarea principal donde buscar las subtareas.
     * @param titulo Texto a buscar en el título de la subtarea.
     * @return Lista de subtareas de la tarea que coinciden con el criterio.
     */
    @Query("SELECT s FROM Subtarea s WHERE s.tarea = :tarea AND s.eliminado = false AND (s.titulo LIKE %:titulo%) ORDER BY s.orden ASC")
    List<Subtarea> findByTareaAndTituloContainingAndEliminadoFalseOrderByOrdenAsc(@Param("tarea") TareaPrincipal tarea, @Param("titulo") String titulo);
    
    /**
     * Obtiene todas las subtareas activas de una tarea ordenadas por orden (versión con @Query).
     * @param tarea Tarea principal de la cual se obtienen las subtareas.
     * @return Lista de subtareas activas ordenadas por orden ascendente.
     */
    @Query("SELECT s FROM Subtarea s WHERE s.tarea = :tarea AND s.eliminado = false ORDER BY s.orden ASC")
    List<Subtarea> findSubtareasByTareaAndEliminadoFalseOrderByOrdenAsc(@Param("tarea") TareaPrincipal tarea);
    
    /**
     * Obtiene el orden máximo de las subtareas activas de una tarea.
     * @param tarea Tarea principal de la cual obtener el orden máximo.
     * @return El valor máximo del campo orden o null si no hay subtareas.
     */
    @Query("SELECT MAX(s.orden) FROM Subtarea s WHERE s.tarea = :tarea AND s.eliminado = false")
    Integer findMaxOrdenByTareaAndEliminadoFalse(@Param("tarea") TareaPrincipal tarea);
    
    /**
     * Cuenta las subtareas activas de una tarea por estado Kanban específico.
     * @param tarea Tarea principal de la cual contar las subtareas.
     * @param estadoKanban Estado Kanban a filtrar.
     * @return Número de subtareas con el estado especificado en la tarea.
     */
    @Query("SELECT COUNT(s) FROM Subtarea s WHERE s.tarea = :tarea AND s.estadoKanban = :estadoKanban AND s.eliminado = false")
    long countByTareaAndEstadoKanbanAndEliminadoFalse(@Param("tarea") TareaPrincipal tarea, @Param("estadoKanban") EstadoKanban estadoKanban);
    
    /**
     * Cuenta todas las subtareas activas de una tarea específica.
     * @param tarea Tarea principal de la cual contar las subtareas.
     * @return Número total de subtareas activas en la tarea.
     */
    @Query("SELECT COUNT(s) FROM Subtarea s WHERE s.tarea = :tarea AND s.eliminado = false")
    long countByTareaAndEliminadoFalse(@Param("tarea") TareaPrincipal tarea);
    
    /**
     * Cuenta todas las subtareas activas creadas por un usuario específico.
     * @param creador Usuario que creó las subtareas.
     * @return Número total de subtareas activas creadas por el usuario.
     */
    @Query("SELECT COUNT(s) FROM Subtarea s WHERE s.creador = :creador AND s.eliminado = false")
    long countByCreadorAndEliminadoFalse(@Param("creador") Usuario creador);
    
    /**
     * Obtiene las subtareas activas que vienen después de un orden específico en una tarea.
     * @param tarea Tarea principal donde buscar las subtareas.
     * @param ordenActual Orden actual a partir del cual buscar subtareas siguientes.
     * @return Lista de subtareas con orden mayor al especificado.
     */
    @Query("SELECT s FROM Subtarea s WHERE s.tarea = :tarea AND s.orden > :ordenActual AND s.eliminado = false ORDER BY s.orden ASC")
    List<Subtarea> findSubtareasSiguientesByTareaAndOrdenAndEliminadoFalse(@Param("tarea") TareaPrincipal tarea, @Param("ordenActual") Integer ordenActual);
}
