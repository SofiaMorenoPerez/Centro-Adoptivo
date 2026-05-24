package co.edu.unbosque.centroadoptivo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unbosque.centroadoptivo.entity.Auditoria;
import co.edu.unbosque.centroadoptivo.repository.AuditoriaRepository;

/**
 * Servicio para gestionar el registro de auditoría del sistema.
 * Permite registrar y consultar las acciones realizadas por los usuarios.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
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
    public void registrar(String usuarioEjecutor, String accion,
            String descripcion, boolean exitoso) {
        Auditoria auditoria = new Auditoria(
                usuarioEjecutor, accion, descripcion, exitoso);
        auditoriaRepo.save(auditoria);
    }

    /**
     * Obtiene todos los registros de auditoría del sistema.
     *
     * @return lista con todos los registros de auditoría
     */
    public List<Auditoria> getAll() {
        return auditoriaRepo.findAll();
    }

    /**
     * Obtiene los registros de auditoría de un usuario específico.
     *
     * @param username username del usuario a consultar
     * @return lista de registros de auditoría del usuario
     */
    public List<Auditoria> getByUsuario(String username) {
        return auditoriaRepo.findByUsuarioEjecutor(username);
    }

    /**
     * Obtiene los registros de auditoría filtrados por tipo de acción.
     *
     * @param accion tipo de acción a consultar (ej: CREATE_USER, DELETE_ANIMAL)
     * @return lista de registros de auditoría con esa acción
     */
    public List<Auditoria> getByAccion(String accion) {
        return auditoriaRepo.findByAccion(accion);
    }

    /**
     * Obtiene los registros de auditoría filtrados por resultado.
     *
     * @param exitoso true para obtener acciones exitosas,
     *                false para obtener acciones fallidas
     * @return lista de registros de auditoría con ese resultado
     */
    public List<Auditoria> getByExitoso(boolean exitoso) {
        return auditoriaRepo.findByExitoso(exitoso);
    }
}