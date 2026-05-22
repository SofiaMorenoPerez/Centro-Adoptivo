package co.edu.unbosque.centroadoptivo.repository;

import co.edu.unbosque.centroadoptivo.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
	
    List<Notificacion> findByDestinatarioId(Long destinatarioId);
    List<Notificacion> findByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
    long countByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
}