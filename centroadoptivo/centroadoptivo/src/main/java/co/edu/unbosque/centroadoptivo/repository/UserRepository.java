package co.edu.unbosque.centroadoptivo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.centroadoptivo.entity.User;

/**
 * Repositorio JPA para la entidad {@link User}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para gestionar los usuarios del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su username.
     * Usado en autenticación JWT y validaciones de unicidad.
     *
     * @param username username del usuario a buscar
     * @return {@link Optional} con el usuario si existe, o vacío si no
     */
    Optional<User> findByUsername(String username);

    /**
     * Elimina un usuario por su username.
     *
     * @param username username del usuario a eliminar
     */
    void deleteByUsername(String username);

    /**
     * Busca un usuario por su email.
     * Usado para verificar que el email no esté en uso al registrar
     * o actualizar un usuario.
     *
     * @param email email del usuario a buscar
     * @return {@link Optional} con el usuario si existe, o vacío si no
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el username indicado.
     * Usado por el {@link co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion}
     * para validar la existencia de usuarios antes de operar sobre ellos.
     *
     * @param username username a verificar
     * @return {@code true} si existe un usuario con ese username,
     *         {@code false} si no
     */
    boolean existsByUsername(String username);
}