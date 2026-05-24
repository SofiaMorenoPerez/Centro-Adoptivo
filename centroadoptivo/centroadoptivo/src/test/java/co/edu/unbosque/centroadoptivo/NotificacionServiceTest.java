package co.edu.unbosque.centroadoptivo;

import co.edu.unbosque.centroadoptivo.dto.NotificacionDTO;
import co.edu.unbosque.centroadoptivo.entity.Notificacion;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.exception.NotificacionNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.repository.NotificacionRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.service.NotificacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link NotificacionService}.
 * <p>
 * Verifica el comportamiento del servicio de notificaciones usando Mockito para
 * simular los repositorios {@link NotificacionRepository} y {@link UserRepository},
 * sin necesidad de levantar el contexto de Spring.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    /** Mock del repositorio de notificaciones inyectado en el servicio. */
    @Mock private NotificacionRepository notificacionRepository;

    /** Mock del repositorio de usuarios inyectado en el servicio. */
    @Mock private UserRepository userRepository;

    /** Instancia del servicio bajo prueba con dependencias mockeadas. */
    @InjectMocks private NotificacionService notificacionService;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Crea un usuario de muestra con id {@code 1} y username {@code "juan123"}.
     *
     * @return instancia de {@link User} con datos predefinidos
     */
    private User sampleUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("juan123");
        return u;
    }

    /**
     * Crea una notificación de muestra asociada al usuario dado, con id {@code 10}.
     *
     * @param user usuario destinatario de la notificación
     * @return instancia de {@link Notificacion} con datos predefinidos
     */
    private Notificacion sampleNotif(User user) {
        Notificacion n = new Notificacion("Mensaje de prueba", user);
        n.setId(10L);
        return n;
    }

    

    /**
     * Verifica que al enviar una notificación, se guarda con el mensaje,
     * destinatario correctos y el campo {@code leida} en {@code false}.
     */
    @Test
    @DisplayName("enviar: guarda notificacion con mensaje y destinatario correctos")
    void enviar_guardaNotificacion() {
        User user = sampleUser();
        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);

        notificacionService.enviar("Hola usuario", user);

        verify(notificacionRepository).save(captor.capture());
        Notificacion guardada = captor.getValue();
        assertEquals("Hola usuario", guardada.getMensaje());
        assertEquals(user, guardada.getDestinatario());
        assertFalse(guardada.isLeida());
    }

    /**
     * Verifica que el método {@code enviar} puede invocarse múltiples veces
     * y cada llamada genera un {@code save} independiente en el repositorio.
     */
    @Test
    @DisplayName("enviar: se puede llamar multiples veces")
    void enviar_multiplesVeces_guardaMultiples() {
        User user = sampleUser();
        notificacionService.enviar("Msg 1", user);
        notificacionService.enviar("Msg 2", user);
        verify(notificacionRepository, times(2)).save(any(Notificacion.class));
    }

    
    /**
     * Verifica que {@code obtenerMisNotificaciones} retorna la lista de DTOs
     * correctamente mapeados cuando el usuario existe y tiene notificaciones.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("obtenerMisNotificaciones: usuario valido → lista de DTOs")
    void obtenerMisNotificaciones_usuarioValido_retornaLista() throws UserNotFoundException {
        User user = sampleUser();
        Notificacion n = sampleNotif(user);

        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.findByDestinatarioId(1L)).thenReturn(List.of(n));

        List<NotificacionDTO> result = notificacionService.obtenerMisNotificaciones("juan123");

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals("Mensaje de prueba", result.get(0).getMensaje());
        assertEquals(1L, result.get(0).getDestinatarioId());
        assertFalse(result.get(0).isLeida());
    }

    /**
     * Verifica que {@code obtenerMisNotificaciones} lanza {@link UserNotFoundException}
     * cuando el usuario no existe en el sistema.
     */
    @Test
    @DisplayName("obtenerMisNotificaciones: usuario inexistente → UserNotFoundException")
    void obtenerMisNotificaciones_usuarioInexistente_lanzaExcepcion() {
        when(userRepository.existsByUsername("fantasma")).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> notificacionService.obtenerMisNotificaciones("fantasma"));
    }

    /**
     * Verifica que {@code obtenerMisNotificaciones} retorna una lista vacía
     * cuando el usuario existe pero no tiene notificaciones.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("obtenerMisNotificaciones: sin notificaciones → lista vacia")
    void obtenerMisNotificaciones_sinNotificaciones_listaVacia() throws UserNotFoundException {
        User user = sampleUser();
        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.findByDestinatarioId(1L)).thenReturn(List.of());

        assertTrue(notificacionService.obtenerMisNotificaciones("juan123").isEmpty());
    }

    

    /**
     * Verifica que {@code obtenerNoLeidas} retorna únicamente las notificaciones
     * no leídas del usuario cuando el usuario existe.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("obtenerNoLeidas: usuario valido → solo no leidas")
    void obtenerNoLeidas_usuarioValido_retornaSoloNoLeidas() throws UserNotFoundException {
        User user = sampleUser();
        Notificacion n = sampleNotif(user);

        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of(n));

        List<NotificacionDTO> result = notificacionService.obtenerNoLeidas("juan123");

        assertEquals(1, result.size());
        assertFalse(result.get(0).isLeida());
    }

    /**
     * Verifica que {@code obtenerNoLeidas} lanza {@link UserNotFoundException}
     * cuando el usuario no existe en el sistema.
     */
    @Test
    @DisplayName("obtenerNoLeidas: usuario inexistente → UserNotFoundException")
    void obtenerNoLeidas_usuarioInexistente_lanzaExcepcion() {
        when(userRepository.existsByUsername("fantasma")).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> notificacionService.obtenerNoLeidas("fantasma"));
    }

    /**
     * Verifica que {@code obtenerNoLeidas} retorna una lista vacía
     * cuando todas las notificaciones del usuario ya han sido leídas.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("obtenerNoLeidas: todas leidas → lista vacia")
    void obtenerNoLeidas_todasLeidas_listaVacia() throws UserNotFoundException {
        User user = sampleUser();
        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of());

        assertTrue(notificacionService.obtenerNoLeidas("juan123").isEmpty());
    }

    

    /**
     * Verifica que {@code contarNoLeidas} retorna el conteo correcto
     * de notificaciones no leídas cuando el usuario tiene pendientes.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("contarNoLeidas: usuario con 3 no leidas → 3")
    void contarNoLeidas_retornaConteo() throws UserNotFoundException {
        User user = sampleUser();
        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.countByDestinatarioIdAndLeida(1L, false)).thenReturn(3L);

        assertEquals(3L, notificacionService.contarNoLeidas("juan123"));
    }

    /**
     * Verifica que {@code contarNoLeidas} lanza {@link UserNotFoundException}
     * cuando el usuario no existe en el sistema.
     */
    @Test
    @DisplayName("contarNoLeidas: usuario inexistente → UserNotFoundException")
    void contarNoLeidas_usuarioInexistente_lanzaExcepcion() {
        when(userRepository.existsByUsername("nadie")).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> notificacionService.contarNoLeidas("nadie"));
    }

    /**
     * Verifica que {@code contarNoLeidas} retorna {@code 0}
     * cuando el usuario no tiene notificaciones sin leer.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("contarNoLeidas: sin no leidas → 0")
    void contarNoLeidas_sinNoLeidas_retornaCero() throws UserNotFoundException {
        User user = sampleUser();
        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.countByDestinatarioIdAndLeida(1L, false)).thenReturn(0L);

        assertEquals(0L, notificacionService.contarNoLeidas("juan123"));
    }

    

    /**
     * Verifica que {@code marcarComoLeida} actualiza el campo {@code leida}
     * a {@code true} y persiste el cambio cuando la notificación existe.
     *
     * @throws NotificacionNoEncontradaException no se espera en este escenario
     */
    @Test
    @DisplayName("marcarComoLeida: notificacion existe → marca leida y guarda")
    void marcarComoLeida_existe_marcaYGuarda() throws NotificacionNoEncontradaException {
        User user = sampleUser();
        Notificacion n = sampleNotif(user);
        assertFalse(n.isLeida());

        when(notificacionRepository.existsById(10L)).thenReturn(true);
        when(notificacionRepository.findById(10L)).thenReturn(Optional.of(n));

        notificacionService.marcarComoLeida(10L);

        assertTrue(n.isLeida());
        verify(notificacionRepository).save(n);
    }

    /**
     * Verifica que {@code marcarComoLeida} lanza {@link NotificacionNoEncontradaException}
     * cuando la notificación con el id indicado no existe.
     */
    @Test
    @DisplayName("marcarComoLeida: no existe → NotificacionNoEncontradaException")
    void marcarComoLeida_noExiste_lanzaExcepcion() {
        when(notificacionRepository.existsById(99L)).thenReturn(false);
        assertThrows(NotificacionNoEncontradaException.class,
                () -> notificacionService.marcarComoLeida(99L));
    }

   

    /**
     * Verifica que {@code marcarTodasComoLeidas} marca todas las notificaciones
     * no leídas como leídas y las persiste mediante {@code saveAll}.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("marcarTodasComoLeidas: dos no leidas → ambas marcadas y guardadas")
    void marcarTodasComoLeidas_dosNoLeidas_marcaAmbas() throws UserNotFoundException {
        User user = sampleUser();
        Notificacion n1 = sampleNotif(user);
        Notificacion n2 = new Notificacion("Otro mensaje", user);
        n2.setId(11L);

        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of(n1, n2));

        notificacionService.marcarTodasComoLeidas("juan123");

        assertTrue(n1.isLeida());
        assertTrue(n2.isLeida());
        verify(notificacionRepository).saveAll(List.of(n1, n2));
    }

    /**
     * Verifica que {@code marcarTodasComoLeidas} lanza {@link UserNotFoundException}
     * cuando el usuario no existe en el sistema.
     */
    @Test
    @DisplayName("marcarTodasComoLeidas: usuario inexistente → UserNotFoundException")
    void marcarTodasComoLeidas_usuarioInexistente_lanzaExcepcion() {
        when(userRepository.existsByUsername("nadie")).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> notificacionService.marcarTodasComoLeidas("nadie"));
    }

    /**
     * Verifica que {@code marcarTodasComoLeidas} invoca {@code saveAll} con una
     * lista vacía cuando el usuario no tiene notificaciones sin leer.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("marcarTodasComoLeidas: sin no leidas → saveAll con lista vacia")
    void marcarTodasComoLeidas_sinNoLeidas_saveAllVacio() throws UserNotFoundException {
        User user = sampleUser();
        when(userRepository.existsByUsername("juan123")).thenReturn(true);
        when(userRepository.findByUsername("juan123")).thenReturn(Optional.of(user));
        when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of());

        notificacionService.marcarTodasComoLeidas("juan123");

        verify(notificacionRepository).saveAll(List.of());
    }
}