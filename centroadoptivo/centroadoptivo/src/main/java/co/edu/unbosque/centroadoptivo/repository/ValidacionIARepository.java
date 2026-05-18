package co.edu.unbosque.centroadoptivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.centroadoptivo.entity.ValidacionIA;
import java.util.List;
import java.util.Optional;

public interface ValidacionIARepository extends JpaRepository<ValidacionIA, Long> {
	
    List<ValidacionIA> findByAnimalId(long animalId);
    Optional<ValidacionIA> findTopByAnimalIdOrderByFechaValidacionDesc(long animalId);
}