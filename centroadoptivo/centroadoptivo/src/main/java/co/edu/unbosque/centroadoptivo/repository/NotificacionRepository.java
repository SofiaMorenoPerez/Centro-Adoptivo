package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Notificacion}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para gestionar las notificaciones
 * de los usuarios del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Obtiene todas las notificaciones de un usuario destinatario específico.
     *
     * @param destinatarioId identificador del usuario destinatario
     * @return lista de notificaciones del usuario
     */
    List<Notificacion> findByDestinatarioId(Long destinatarioId);

    /**
     * Obtiene las notificaciones de un usuario filtradas por estado de lectura.
     *
     * @param destinatarioId identificador del usuario destinatario
     * @param leida          {@code true} para obtener las leídas,
     *                       {@code false} para obtener las no leídas
     * @return lista de notificaciones del usuario con ese estado de lectura
     */
    List<Notificacion> findByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);

    /**
     * Cuenta las notificaciones de un usuario filtradas por estado de lectura.
     * Usado por el ícono de campana en el frontend para mostrar
     * el número de notificaciones pendientes.
     *
     * @param destinatarioId identificador del usuario destinatario
     * @param leida          {@code true} para contar las leídas,
     *                       {@code false} para contar las no leídas
     * @return número de notificaciones con ese estado de lectura
     */
    long countByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
}