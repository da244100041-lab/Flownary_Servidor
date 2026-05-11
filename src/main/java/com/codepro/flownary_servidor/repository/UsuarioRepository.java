package com.codepro.flownary_servidor.repository;

import com.codepro.flownary_servidor.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de entidades Usuario.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre la tabla de usuarios del sistema.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email Correo electrónico del usuario a buscar.
     * @return Optional con el usuario encontrado o vacío si no existe.
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Busca un usuario activo por su correo electrónico.
     * Filtra los usuarios que no han sido eliminados lógicamente.
     * @param email Correo electrónico del usuario a buscar.
     * @return Optional con el usuario activo encontrado o vacío si no existe.
     */
    Optional<Usuario> findByEmailAndEliminadoFalse(String email);
    
    /**
     * Obtiene todos los usuarios activos del sistema.
     * Excluye los usuarios marcados como eliminados.
     * @return Lista de usuarios activos.
     */
    List<Usuario> findByEliminadoFalse();
    
    /**
     * Busca usuarios activos por nombre o apellido.
     * Realiza búsqueda parcial en ambos campos (userName y userLastname).
     * @param nombre Texto a buscar en nombre o apellido.
     * @return Lista de usuarios que coinciden con el criterio de búsqueda.
     */
    @Query("SELECT u FROM Usuario u WHERE u.eliminado = false AND (u.userName LIKE %:nombre% OR u.userLasname LIKE %:nombre%)")
    List<Usuario> findByNombreContainingAndEliminadoFalse(@Param("nombre") String nombre);
    
    /**
     * Busca un usuario activo por su número de teléfono.
     * @param telefono Número de teléfono del usuario a buscar.
     * @return Optional con el usuario encontrado o vacío si no existe.
     */
    @Query("SELECT u FROM Usuario u WHERE u.eliminado = false AND u.telefono = :telefono")
    Optional<Usuario> findByTelefonoAndEliminadoFalse(@Param("telefono") String telefono);
    
    /**
     * Verifica si existe un usuario con el correo electrónico especificado.
     * Incluye usuarios eliminados lógicamente.
     * @param email Correo electrónico a verificar.
     * @return true si existe un usuario con ese email, false en caso contrario.
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un usuario activo con el correo electrónico especificado.
     * Excluye usuarios eliminados lógicamente.
     * @param email Correo electrónico a verificar.
     * @return true si existe un usuario activo con ese email, false en caso contrario.
     */
    boolean existsByEmailAndEliminadoFalse(String email);
    
    /**
     * Busca usuarios activos por una lista de números de teléfono.
     * Filtra los usuarios que no han sido eliminados lógicamente.
     * @param telefonos Lista de números de teléfono a buscar.
     * @return Lista de usuarios que coinciden con los teléfonos proporcionados.
     */
    /**
     * Busca usuarios activos por una lista de números de teléfono.
     * Filtra los usuarios que no han sido eliminados lógicamente.
     * @param telefonos Lista de números de teléfono a buscar.
     * @return Lista de usuarios que coinciden con los teléfonos proporcionados.
     */
    @Query("SELECT u FROM Usuario u WHERE u.eliminado = false AND u.telefono IN :telefonos")
    List<Usuario> findByTelefonoInAndEliminadoFalse(@Param("telefonos") List<String> telefonos);
}
