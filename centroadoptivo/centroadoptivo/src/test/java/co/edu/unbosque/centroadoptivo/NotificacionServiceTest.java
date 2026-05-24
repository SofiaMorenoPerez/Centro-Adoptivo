package co.edu.unbosque.centroadoptivo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.centroadoptivo.dto.NotificacionDTO;
import co.edu.unbosque.centroadoptivo.entity.Notificacion;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.exception.NotificacionNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.repository.NotificacionRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.service.NotificacionService;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionService — Pruebas unitarias")
class NotificacionServiceTest {

    @Mock private NotificacionRepository notificacionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    // ── Helpers ───────────────────────────────────────────────
    private User crearUsuario() {
        User u = new User();
        u.setId(1L);
        u.setUsername("juanito");
        return u;
    }

    private Notificacion crearNotificacion(User destinatario, boolean leida) {
        Notificacion n = new Notificacion("Mensaje de prueba", destinatario);
        n.setId(10L);
        n.setLeida(leida);
        return n;
    }

    // ═══════════════════════════════════════════════════════════
    // ENVIAR
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("enviar()")
    class EnviarTest {

        @Test
        @DisplayName("Debe guardar la notificación correctamente")
        void enviar_guarda_notificacion() {
            User destinatario = crearUsuario();
            Notificacion n = crearNotificacion(destinatario, false);

            when(notificacionRepository.save(any(Notificacion.class))).thenReturn(n);

            notificacionService.enviar("Mensaje de prueba", destinatario);

            verify(notificacionRepository).save(any(Notificacion.class));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // OBTENER MIS NOTIFICACIONES
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("obtenerMisNotificaciones()")
    class ObtenerMisNotificacionesTest {

        @Test
        @DisplayName("Debe retornar lista de notificaciones del usuario")
        void obtener_notificaciones_exitoso() throws Exception {
            User usuario = crearUsuario();
            Notificacion n = crearNotificacion(usuario, false);

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByDestinatarioId(1L)).thenReturn(List.of(n));

            List<NotificacionDTO> resultado = notificacionService
                    .obtenerMisNotificaciones("juanito");

            assertEquals(1, resultado.size());
            assertEquals("Mensaje de prueba", resultado.get(0).getMensaje());
            assertFalse(resultado.get(0).isLeida());
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no tiene notificaciones")
        void obtener_notificaciones_vacio() throws Exception {
            User usuario = crearUsuario();

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByDestinatarioId(1L)).thenReturn(List.of());

            List<NotificacionDTO> resultado = notificacionService
                    .obtenerMisNotificaciones("juanito");

            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si el usuario no existe")
        void obtener_notificaciones_usuario_no_existe() {
            when(userRepository.existsByUsername("noexiste")).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                notificacionService.obtenerMisNotificaciones("noexiste"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // OBTENER NO LEÍDAS
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("obtenerNoLeidas()")
    class ObtenerNoLeidasTest {

        @Test
        @DisplayName("Debe retornar solo las notificaciones no leídas")
        void obtener_no_leidas_exitoso() throws Exception {
            User usuario = crearUsuario();
            Notificacion noLeida = crearNotificacion(usuario, false);
            Notificacion leida = crearNotificacion(usuario, true);
            leida.setId(11L);

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of(noLeida));

            List<NotificacionDTO> resultado = notificacionService.obtenerNoLeidas("juanito");

            assertEquals(1, resultado.size());
            assertFalse(resultado.get(0).isLeida());
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si el usuario no existe")
        void obtener_no_leidas_usuario_no_existe() {
            when(userRepository.existsByUsername("noexiste")).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                notificacionService.obtenerNoLeidas("noexiste"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CONTAR NO LEÍDAS
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("contarNoLeidas()")
    class ContarNoLeidasTest {

        @Test
        @DisplayName("Debe retornar el número correcto de no leídas")
        void contar_no_leidas_exitoso() throws Exception {
            User usuario = crearUsuario();

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.countByDestinatarioIdAndLeida(1L, false)).thenReturn(3L);

            long resultado = notificacionService.contarNoLeidas("juanito");

            assertEquals(3L, resultado);
        }

        @Test
        @DisplayName("Debe retornar 0 si todas están leídas")
        void contar_no_leidas_cero() throws Exception {
            User usuario = crearUsuario();

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.countByDestinatarioIdAndLeida(1L, false)).thenReturn(0L);

            long resultado = notificacionService.contarNoLeidas("juanito");

            assertEquals(0L, resultado);
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si el usuario no existe")
        void contar_usuario_no_existe() {
            when(userRepository.existsByUsername("noexiste")).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                notificacionService.contarNoLeidas("noexiste"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // MARCAR COMO LEÍDA
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("marcarComoLeida()")
    class MarcarComoLeidaTest {

        @Test
        @DisplayName("Debe marcar la notificación como leída")
        void marcar_leida_exitoso() throws Exception {
            User usuario = crearUsuario();
            Notificacion n = crearNotificacion(usuario, false);

            when(notificacionRepository.existsById(10L)).thenReturn(true);
            when(notificacionRepository.findById(10L)).thenReturn(Optional.of(n));
            when(notificacionRepository.save(any())).thenReturn(n);

            notificacionService.marcarComoLeida(10L);

            assertTrue(n.isLeida());
            verify(notificacionRepository).save(n);
        }

        @Test
        @DisplayName("Debe lanzar NotificacionNoEncontradaException si no existe")
        void marcar_leida_no_existe() {
            when(notificacionRepository.existsById(99L)).thenReturn(false);

            assertThrows(NotificacionNoEncontradaException.class, () ->
                notificacionService.marcarComoLeida(99L));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // MARCAR TODAS COMO LEÍDAS
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("marcarTodasComoLeidas()")
    class MarcarTodasComoLeidasTest {

        @Test
        @DisplayName("Debe marcar todas las notificaciones como leídas")
        void marcar_todas_exitoso() throws Exception {
            User usuario = crearUsuario();
            Notificacion n1 = crearNotificacion(usuario, false);
            Notificacion n2 = crearNotificacion(usuario, false);
            n2.setId(11L);

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of(n1, n2));

            notificacionService.marcarTodasComoLeidas("juanito");

            assertTrue(n1.isLeida());
            assertTrue(n2.isLeida());
            verify(notificacionRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("No debe guardar nada si no hay notificaciones no leídas")
        void marcar_todas_sin_pendientes() throws Exception {
            User usuario = crearUsuario();

            when(userRepository.existsByUsername("juanito")).thenReturn(true);
            when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of());

            notificacionService.marcarTodasComoLeidas("juanito");

            verify(notificacionRepository).saveAll(List.of());
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si el usuario no existe")
        void marcar_todas_usuario_no_existe() {
            when(userRepository.existsByUsername("noexiste")).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                notificacionService.marcarTodasComoLeidas("noexiste"));
        }
    }
}