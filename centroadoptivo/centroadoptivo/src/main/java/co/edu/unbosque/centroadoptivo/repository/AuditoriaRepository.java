package co.edu.unbosque.centroadoptivo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.centroadoptivo.entity.Auditoria;

/**
 * Repositorio JPA para la entidad {@link Auditoria}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para filtrar registros de auditoría.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    /**
     * Obtiene todos los registros de auditoría de un usuario específico.
     *
     * @param usuarioEjecutor username del usuario que ejecutó la acción
     * @return lista de registros de auditoría de ese usuario
     */
    List<Auditoria> findByUsuarioEjecutor(String usuarioEjecutor);

    /**
     * Obtiene todos los registros de auditoría de un tipo de acción específico.
     *
     * @param accion tipo de acción a filtrar (ej: CREATE_USER, DELETE_ANIMAL)
     * @return lista de registros de auditoría con esa acción
     */
    List<Auditoria> findByAccion(String accion);

    /**
     * Obtiene todos los registros de auditoría filtrados por resultado.
     *
     * @param exitoso {@code true} para obtener acciones exitosas,
     *                {@code false} para obtener acciones fallidas
     * @return lista de registros de auditoría con ese resultado
     */
    List<Auditoria> findByExitoso(boolean exitoso);
}