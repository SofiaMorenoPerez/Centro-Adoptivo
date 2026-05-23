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

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UserRepository userRepository;

    // Llamado internamente desde SolicitudAdopcionService
    public void enviar(String mensaje, User destinatario) {
        notificacionRepository.save(new Notificacion(mensaje, destinatario));
    }

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

    public List<NotificacionDTO> obtenerNoLeidas(String usernameActual)
            throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        return notificacionRepository.findByDestinatarioIdAndLeida(user.getId(), false)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public long contarNoLeidas(String usernameActual) throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        return notificacionRepository.countByDestinatarioIdAndLeida(user.getId(), false);
    }

    public void marcarComoLeida(Long id) throws NotificacionNoEncontradaException {
        LanzadorDeExcepcion.verificarNotificacionExiste(
                notificacionRepository.existsById(id));
        Notificacion n = notificacionRepository.findById(id).get();
        n.setLeida(true);
        notificacionRepository.save(n);
    }

    public void marcarTodasComoLeidas(String usernameActual) throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User user = userRepository.findByUsername(usernameActual).get();
        List<Notificacion> noLeidas = notificacionRepository
                .findByDestinatarioIdAndLeida(user.getId(), false);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }

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