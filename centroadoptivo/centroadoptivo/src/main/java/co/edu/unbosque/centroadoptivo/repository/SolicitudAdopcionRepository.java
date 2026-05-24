package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link SolicitudAdopcion}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para gestionar las solicitudes
 * de adopción del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {

    /**
     * Obtiene todas las solicitudes de adopción con un estado específico.
     *
     * @param status estado de la solicitud (PENDING, APPROVED, REJECTED)
     * @return lista de solicitudes con ese estado
     */
    List<SolicitudAdopcion> findByStatus(RequestStatus status);

    /**
     * Obtiene todas las solicitudes de adopción realizadas por un adoptante específico.
     *
     * @param adopterId identificador del usuario adoptante
     * @return lista de solicitudes del adoptante
     */
    List<SolicitudAdopcion> findByAdopterId(Long adopterId);

    /**
     * Obtiene todas las solicitudes de adopción asociadas a un animal específico.
     *
     * @param animalId identificador del animal
     * @return lista de solicitudes para ese animal
     */
    List<SolicitudAdopcion> findByAnimalId(Long animalId);

    /**
     * Verifica si ya existe una solicitud de adopción de un usuario
     * para un animal específico con un estado determinado.
     * Usado para evitar solicitudes duplicadas pendientes.
     *
     * @param animalId  identificador del animal
     * @param adopterId identificador del adoptante
     * @param status    estado a verificar (generalmente PENDING)
     * @return {@code true} si ya existe la solicitud, {@code false} si no
     */
    boolean existsByAnimalIdAndAdopterIdAndStatus(
            Long animalId, Long adopterId, RequestStatus status);
}