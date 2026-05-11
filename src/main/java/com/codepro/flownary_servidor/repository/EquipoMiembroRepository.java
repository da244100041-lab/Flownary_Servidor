package com.codepro.flownary_servidor.repository;

import com.codepro.flownary_servidor.entity.Equipo;
import com.codepro.flownary_servidor.entity.EquipoMiembro;
import com.codepro.flownary_servidor.entity.Rol;
import com.codepro.flownary_servidor.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de entidades EquipoMiembro.
 * Maneja la relación muchos-a-muchos entre equipos y usuarios,
 * incluyendo roles y membresías activas del sistema.
 */
@Repository
public interface EquipoMiembroRepository extends JpaRepository<EquipoMiembro, String> {
    
    /**
     * Obtiene todos los miembros activos de un equipo específico.
     * @param equipo Equipo del cual se obtienen los miembros.
     * @return Lista de miembros activos del equipo.
     */
    List<EquipoMiembro> findByEquipoAndEliminadoFalse(Equipo equipo);
    
    /**
     * Obtiene todas las membresías activas de un usuario específico.
     * @param usuario Usuario del cual se obtienen las membresías.
     * @return Lista de equipos donde el usuario tiene membresía activa.
     */
    List<EquipoMiembro> findByUsuarioAndEliminadoFalse(Usuario usuario);
    
    /**
     * Busca la membresía activa de un usuario en un equipo específico.
     * @param equipo Equipo donde buscar la membresía.
     * @param usuario Usuario del cual buscar la membresía.
     * @return Optional con la membresía encontrada o vacío si no existe.
     */
    Optional<EquipoMiembro> findByEquipoAndUsuarioAndEliminadoFalse(Equipo equipo, Usuario usuario);
    
    /**
     * Obtiene los miembros activos de un equipo con un rol específico.
     * @param equipo Equipo del cual se obtienen los miembros.
     * @param rol Rol a filtrar (ADMINISTRADOR o COLABORADOR).
     * @return Lista de miembros del equipo con el rol especificado.
     */
    List<EquipoMiembro> findByEquipoAndRolAndEliminadoFalse(Equipo equipo, Rol rol);
    
    /**
     * Obtiene los miembros activos de un equipo con un rol específico (versión con @Query).
     * @param equipo Equipo del cual se obtienen los miembros.
     * @param rol Rol a filtrar (ADMINISTRADOR o COLABORADOR).
     * @return Lista de miembros del equipo con el rol especificado.
     */
    @Query("SELECT em FROM EquipoMiembro em WHERE em.equipo = :equipo AND em.eliminado = false AND em.rol = :rol")
    List<EquipoMiembro> findMiembrosByEquipoAndRolAndEliminadoFalse(@Param("equipo") Equipo equipo, @Param("rol") Rol rol);
    
    /**
     * Cuenta los miembros activos de un equipo específico.
     * @param equipo Equipo del cual contar los miembros.
     * @return Número de miembros activos en el equipo.
     */
    @Query("SELECT COUNT(em) FROM EquipoMiembro em WHERE em.equipo = :equipo AND em.eliminado = false")
    long countByEquipoAndEliminadoFalse(@Param("equipo") Equipo equipo);
    
    /**
     * Cuenta los miembros activos de un equipo con un rol específico.
     * @param equipo Equipo del cual contar los miembros.
     * @param rol Rol a filtrar.
     * @return Número de miembros con el rol especificado en el equipo.
     */
    @Query("SELECT COUNT(em) FROM EquipoMiembro em WHERE em.equipo = :equipo AND em.rol = :rol AND em.eliminado = false")
    long countByEquipoAndRolAndEliminadoFalse(@Param("equipo") Equipo equipo, @Param("rol") Rol rol);
    
    /**
     * Verifica si un usuario tiene membresía activa en un equipo específico.
     * @param equipo Equipo donde verificar la membresía.
     * @param usuario Usuario del cual verificar la membresía.
     * @return true si el usuario tiene membresía activa, false en caso contrario.
     */
    boolean existsByEquipoAndUsuarioAndEliminadoFalse(Equipo equipo, Usuario usuario);
    
    /**
     * Verifica si un usuario tiene un rol específico en un equipo.
     * @param equipo Equipo donde verificar el rol.
     * @param usuario Usuario del cual verificar el rol.
     * @param rol Rol a verificar.
     * @return true si el usuario tiene el rol especificado en el equipo, false en caso contrario.
     */
    @Query("SELECT CASE WHEN COUNT(em) > 0 THEN true ELSE false END FROM EquipoMiembro em WHERE em.equipo = :equipo AND em.usuario = :usuario AND em.rol = :rol AND em.eliminado = false")
    boolean existsByEquipoAndUsuarioAndRolAndEliminadoFalse(@Param("equipo") Equipo equipo, @Param("usuario") Usuario usuario, @Param("rol") Rol rol);
    
    /**
     * Obtiene los miembros activos de un equipo con información del usuario concatenada.
     * Utiliza JOIN FETCH para optimizar la consulta y evitar N+1 queries.
     * 
     * @param idEquipo ID del equipo del cual obtener los miembros.
     * @return Lista de miembros activos con información del usuario cargada.
     */
    @Query("SELECT em FROM EquipoMiembro em " +
           "LEFT JOIN FETCH em.usuario " +
           "WHERE em.equipo.idEquipo = :idEquipo AND em.eliminado = false " +
           "ORDER BY em.rol ASC, em.usuario.userName ASC")
    List<EquipoMiembro> findMiembrosActivosByEquipoId(@Param("idEquipo") String idEquipo);
    
    /**
     * Verifica si un usuario es miembro activo de un equipo.
     * @param idEquipo ID del equipo.
     * @param usuario Usuario a verificar.
     * @return true si el usuario es miembro activo del equipo, false en caso contrario.
     */
    @Query("SELECT CASE WHEN COUNT(em) > 0 THEN true ELSE false END FROM EquipoMiembro em WHERE em.equipo.idEquipo = :idEquipo AND em.usuario.idUsuario = :usuarioId AND em.eliminado = false")
    boolean existsByEquipoAndUsuarioAndEliminadoFalse(@Param("idEquipo") String idEquipo, @Param("usuarioId") String usuarioId);
    
    /**
     * Verifica si un usuario es administrador de un equipo.
     * @param idEquipo ID del equipo.
     * @param usuarioId ID del usuario a verificar.
     * @return true si el usuario es administrador del equipo, false en caso contrario.
     */
    @Query("SELECT CASE WHEN COUNT(em) > 0 THEN true ELSE false END FROM EquipoMiembro em WHERE em.equipo.idEquipo = :idEquipo AND em.usuario.idUsuario = :usuarioId AND em.rol = 'ADMINISTRADOR' AND em.eliminado = false")
    boolean esAdministradorDeEquipo(@Param("idEquipo") String idEquipo, @Param("usuarioId") String usuarioId);
}
