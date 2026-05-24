package co.edu.unbosque.centroadoptivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.centroadoptivo.entity.ValidacionIA;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link ValidacionIA}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para acceder a los registros
 * de validación generados por las IAs al registrar un animal.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface ValidacionIARepository extends JpaRepository<ValidacionIA, Long> {

    /**
     * Obtiene todos los registros de validación de IA asociados
     * a un animal específico.
     *
     * @param animalId identificador del animal
     * @return lista de validaciones de IA para ese animal
     */
    List<ValidacionIA> findByAnimalId(long animalId);

    /**
     * Obtiene el registro de validación de IA más reciente
     * asociado a un animal específico, ordenado por fecha de validación
     * de forma descendente.
     * Útil para consultar el último resultado de validación de un animal.
     *
     * @param animalId identificador del animal
     * @return {@link Optional} con la validación más reciente si existe,
     *         o vacío si no se encontró ninguna
     */
    Optional<ValidacionIA> findTopByAnimalIdOrderByFechaValidacionDesc(long animalId);
}