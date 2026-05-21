package co.edu.unbosque.centroadoptivo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.centroadoptivo.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioEjecutor(String usuarioEjecutor);
    List<Auditoria> findByAccion(String accion);
    List<Auditoria> findByExitoso(boolean exitoso);
}