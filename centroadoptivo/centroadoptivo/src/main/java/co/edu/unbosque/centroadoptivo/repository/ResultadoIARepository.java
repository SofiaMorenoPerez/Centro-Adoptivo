package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.ResultadoIA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link ResultadoIA}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para acceder a los resultados
 * de detección generados por las IAs al registrar un animal.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface ResultadoIARepository extends JpaRepository<ResultadoIA, Long> {

    /**
     * Obtiene el resultado de detección de IA asociado a un animal específico.
     *
     * @param animalId identificador del animal
     * @return {@link Optional} con el resultado de IA si existe,
     *         o vacío si no se encontró
     */
    Optional<ResultadoIA> findByAnimalId(Long animalId);
}