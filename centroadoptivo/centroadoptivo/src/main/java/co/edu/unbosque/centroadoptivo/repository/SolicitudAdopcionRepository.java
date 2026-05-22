package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {

    List<SolicitudAdopcion> findByStatus(RequestStatus status);
    List<SolicitudAdopcion> findByAdopterId(Long adopterId);
    List<SolicitudAdopcion> findByAnimalId(Long animalId);
    boolean existsByAnimalIdAndAdopterIdAndStatus(Long animalId, Long adopterId, RequestStatus status);
}