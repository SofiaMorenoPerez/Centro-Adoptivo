package co.edu.unbosque.centroadoptivo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.centroadoptivo.entity.Auditoria;
import co.edu.unbosque.centroadoptivo.repository.AuditoriaRepository;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepo;

    /**
     * Registra una acción en la auditoría.
     *
     * @param usuarioEjecutor Username de quien realizó la acción
     * @param accion          Tipo de acción (ej: CREATE_USER, DELETE_USER)
     * @param descripcion     Descripción legible de lo que ocurrió
     * @param exitoso         Si la operación fue exitosa o no
     */
    public void registrar(String usuarioEjecutor, String accion, String descripcion, boolean exitoso) {
        Auditoria auditoria = new Auditoria(usuarioEjecutor, accion, descripcion, exitoso);
        auditoriaRepo.save(auditoria);
    }

    public List<Auditoria> getAll() {
        return auditoriaRepo.findAll();
    }

    public List<Auditoria> getByUsuario(String username) {
        return auditoriaRepo.findByUsuarioEjecutor(username);
    }

    public List<Auditoria> getByAccion(String accion) {
        return auditoriaRepo.findByAccion(accion);
    }

    public List<Auditoria> getByExitoso(boolean exitoso) {
        return auditoriaRepo.findByExitoso(exitoso);
    }
}