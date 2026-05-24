package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Animal}.
 * Proporciona operaciones CRUD heredadas de {@link JpaRepository}
 * y consultas personalizadas para filtrar animales.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    /**
     * Obtiene todos los animales que tienen un estado específico.
     *
     * @param status estado del animal (AVAILABLE, PENDING, ADOPTED)
     * @return lista de animales con ese estado
     */
    List<Animal> findByStatus(AnimalStatus status);

    /**
     * Obtiene todos los animales publicados por un usuario específico.
     *
     * @param publisherId identificador del usuario publicador
     * @return lista de animales publicados por ese usuario
     */
    List<Animal> findByPublisherId(Long publisherId);
}