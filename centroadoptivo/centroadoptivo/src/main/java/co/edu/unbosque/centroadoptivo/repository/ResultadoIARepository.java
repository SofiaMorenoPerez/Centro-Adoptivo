package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.ResultadoIA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResultadoIARepository extends JpaRepository<ResultadoIA, Long> {
    Optional<ResultadoIA> findByAnimalId(Long animalId);
    
}