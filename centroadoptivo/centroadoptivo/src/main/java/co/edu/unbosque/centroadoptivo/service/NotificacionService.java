package co.edu.unbosque.centroadoptivo.service;

import co.edu.unbosque.centroadoptivo.dto.NotificacionDTO;
import co.edu.unbosque.centroadoptivo.entity.Notificacion;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.NotificacionNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.repository.NotificacionRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio para gestionar las notificaciones del sistema.
 * Permite enviar, consultar y marcar como leídas las notificaciones
 * de los usuarios dentro del centro adoptivo.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Envía una notificación a un usuario destinatario.
     * Es llamado internamente desde {@link SolicitudAdopcionService}
     * cuando se crea, aprueba o rechaza una solicitud de adopción.
     *
     * @param mensaje      contenido de la notificación
     * @param destinatario usuario que recibirá la notificación
     */
    public void enviar(String mensaje, User destinatario) {
        notificacionRepository.save(new Notificacion(mensaje, destinatario));
    }

    /**
     * Obtiene todas las notificaciones del usuario autenticado.
     *
     * @param usernameActual username del usuario autenticado
     * @return lista de {@link NotificacionDTO} del usuario
     * @throws UserNotFoundException si el usuario no existe en el sistema
     */
    public List<NotificacionDTO> obtenerMisNotificaciones(String usernameActual)
            throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        return notificacionRepository.findByDestinatarioId(user.getId())
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene únicamente las notificaciones no leídas del usuario autenticado.
     *
     * @param usernameActual username del usuario autenticado
     * @return lista de {@link NotificacionDTO} no leídas del usuario
     * @throws UserNotFoundException si el usuario no existe en el sistema
     */
    public List<NotificacionDTO> obtenerNoLeidas(String usernameActual)
            throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        return notificacionRepository
                .findByDestinatarioIdAndLeida(user.getId(), false)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Cuenta el número de notificaciones no leídas del usuario autenticado.
     * Es usado por el ícono de campana en el frontend.
     *
     * @param usernameActual username del usuario autenticado
     * @return número de notificaciones no leídas
     * @throws UserNotFoundException si el usuario no existe en el sistema
     */
    public long contarNoLeidas(String usernameActual) throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        return notificacionRepository
                .countByDestinatarioIdAndLeida(user.getId(), false);
    }

    /**
     * Marca una notificación específica como leída.
     *
     * @param id identificador de la notificación a marcar
     * @throws NotificacionNoEncontradaException si la notificación no existe
     */
    public void marcarComoLeida(Long id) throws NotificacionNoEncontradaException {
        LanzadorDeExcepcion.verificarNotificacionExiste(
                notificacionRepository.existsById(id));
        Notificacion n = notificacionRepository.findById(id).get();
        n.setLeida(true);
        notificacionRepository.save(n);
    }

    /**
     * Marca todas las notificaciones no leídas del usuario autenticado como leídas.
     *
     * @param usernameActual username del usuario autenticado
     * @throws UserNotFoundException si el usuario no existe en el sistema
     */
    public void marcarTodasComoLeidas(String usernameActual) throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        List<Notificacion> noLeidas = notificacionRepository
                .findByDestinatarioIdAndLeida(user.getId(), false);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }

    /**
     * Convierte una entidad {@link Notificacion} a su correspondiente
     * {@link NotificacionDTO} para ser enviado al frontend.
     *
     * @param n entidad de notificación a convertir
     * @return {@link NotificacionDTO} con los datos de la notificación
     */
    private NotificacionDTO convertirADTO(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(n.getId());
        dto.setMensaje(n.getMensaje());
        dto.setLeida(n.isLeida());
        dto.setCreadaEn(n.getCreadaEn());
        dto.setDestinatarioId(n.getDestinatario().getId());
        return dto;
    }
}