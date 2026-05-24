package co.edu.unbosque.centroadoptivo;

import co.edu.unbosque.centroadoptivo.dto.SolicitudAdopcionDTO;
import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoDisponibleException;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudDuplicadaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoPendienteException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.repository.AnimalRepository;
import co.edu.unbosque.centroadoptivo.repository.SolicitudAdopcionRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.service.NotificacionService;
import co.edu.unbosque.centroadoptivo.service.SolicitudAdopcionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudAdopcionServiceTest {

    @Mock private SolicitudAdopcionRepository solicitudRepository;
    @Mock private AnimalRepository animalRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificacionService notificacionService;
    @InjectMocks private SolicitudAdopcionService solicitudService;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private User publisher() {
        User u = new User();
        u.setId(1L);
        u.setUsername("publicador");
        return u;
    }

    private User adopter() {
        User u = new User();
        u.setId(2L);
        u.setUsername("adoptante");
        return u;
    }

    private Animal animalDisponible() {
        Animal a = new Animal();
        a.setId(10L);
        a.setName("Firulais");
        a.setStatus(AnimalStatus.AVAILABLE);
        a.setPublisher(publisher());
        return a;
    }

    private SolicitudAdopcion solicitudPendiente(Animal animal, User adopter) {
        SolicitudAdopcion s = new SolicitudAdopcion(animal, adopter,
                LocalDateTime.now(), RequestStatus.PENDING);
        s.setId(100L);
        return s;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // crearSolicitud()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("crearSolicitud: animal no existe → AnimalNoEncontradoException")
    void crearSolicitud_animalNoExiste_lanzaExcepcion() {
        when(animalRepository.existsById(10L)).thenReturn(false);
        assertThrows(AnimalNoEncontradoException.class,
                () -> solicitudService.crearSolicitud(10L, "adoptante"));
    }

    @Test
    @DisplayName("crearSolicitud: animal no disponible → AnimalNoDisponibleException")
    void crearSolicitud_animalNoDispo_lanzaExcepcion() {
        Animal a = animalDisponible();
        a.setStatus(AnimalStatus.ADOPTED);
        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));

        assertThrows(AnimalNoDisponibleException.class,
                () -> solicitudService.crearSolicitud(10L, "adoptante"));
    }

    @Test
    @DisplayName("crearSolicitud: usuario no existe → UserNotFoundException")
    void crearSolicitud_usuarioNoExiste_lanzaExcepcion() {
        Animal a = animalDisponible();
        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));
        when(userRepository.existsByUsername("adoptante")).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> solicitudService.crearSolicitud(10L, "adoptante"));
    }

    @Test
    @DisplayName("crearSolicitud: adoptante es el mismo publicador → AnimalNoDisponibleException")
    void crearSolicitud_adoptanteEsPublicador_lanzaExcepcion() {
        User pub = publisher();
        Animal a = animalDisponible(); // publisher id=1

        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));
        when(userRepository.existsByUsername("publicador")).thenReturn(true);
        when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(pub));

        assertThrows(AnimalNoDisponibleException.class,
                () -> solicitudService.crearSolicitud(10L, "publicador"));
    }

    @Test
    @DisplayName("crearSolicitud: solicitud duplicada → SolicitudDuplicadaException")
    void crearSolicitud_duplicada_lanzaExcepcion() {
        Animal a = animalDisponible();
        User adopter = adopter();

        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));
        when(userRepository.existsByUsername("adoptante")).thenReturn(true);
        when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adopter));
        when(solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                10L, 2L, RequestStatus.PENDING)).thenReturn(true);

        assertThrows(SolicitudDuplicadaException.class,
                () -> solicitudService.crearSolicitud(10L, "adoptante"));
    }

    @Test
    @DisplayName("crearSolicitud: valida → cambia estado animal, guarda solicitud, envía 2 notificaciones")
    void crearSolicitud_valida_guardaYNotifica() throws Exception {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion solicitud = solicitudPendiente(a, adopter);

        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));
        when(userRepository.existsByUsername("adoptante")).thenReturn(true);
        when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adopter));
        when(solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                10L, 2L, RequestStatus.PENDING)).thenReturn(false);
        when(solicitudRepository.save(any(SolicitudAdopcion.class))).thenReturn(solicitud);

        SolicitudAdopcionDTO result = solicitudService.crearSolicitud(10L, "adoptante");

        // Animal pasa a PENDING
        assertEquals(AnimalStatus.PENDING, a.getStatus());
        verify(animalRepository).save(a);

        // Solicitud guardada
        verify(solicitudRepository).save(any(SolicitudAdopcion.class));

        // Dos notificaciones enviadas (al publicador y al adoptante)
        verify(notificacionService, times(2)).enviar(anyString(), any(User.class));

        // DTO bien formado
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getAnimalId());
        assertEquals("Firulais", result.getAnimalName());
        assertEquals(2L, result.getAdopterId());
        assertEquals("adoptante", result.getAdopterUsername());
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("crearSolicitud: notificacion al publicador contiene nombre del adoptante")
    void crearSolicitud_notificacionPublicadorContieneAdoptante() throws Exception {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion solicitud = solicitudPendiente(a, adopter);

        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));
        when(userRepository.existsByUsername("adoptante")).thenReturn(true);
        when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adopter));
        when(solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                10L, 2L, RequestStatus.PENDING)).thenReturn(false);
        when(solicitudRepository.save(any(SolicitudAdopcion.class))).thenReturn(solicitud);

        solicitudService.crearSolicitud(10L, "adoptante");

        verify(notificacionService).enviar(
                contains("adoptante"),
                eq(publisher())
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    // aprobarSolicitud()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("aprobarSolicitud: solicitud no existe → SolicitudNoEncontradaException")
    void aprobarSolicitud_noExiste_lanzaExcepcion() {
        when(solicitudRepository.existsById(100L)).thenReturn(false);
        assertThrows(SolicitudNoEncontradaException.class,
                () -> solicitudService.aprobarSolicitud(100L));
    }

    @Test
    @DisplayName("aprobarSolicitud: no esta pendiente → SolicitudNoPendienteException")
    void aprobarSolicitud_noPendiente_lanzaExcepcion() {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);
        s.setStatus(RequestStatus.APPROVED);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));

        assertThrows(SolicitudNoPendienteException.class,
                () -> solicitudService.aprobarSolicitud(100L));
    }

    @Test
    @DisplayName("aprobarSolicitud: pendiente → APPROVED, animal ADOPTED, 2 notificaciones")
    void aprobarSolicitud_pendiente_aprueba() throws Exception {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));
        when(solicitudRepository.save(s)).thenReturn(s);

        SolicitudAdopcionDTO result = solicitudService.aprobarSolicitud(100L);

        // Estado de solicitud
        assertEquals(RequestStatus.APPROVED, s.getStatus());
        assertNotNull(s.getResolutionDate());

        // Animal adoptado con adoptante asignado
        assertEquals(AnimalStatus.ADOPTED, a.getStatus());
        assertEquals(adopter, a.getAdopter());
        verify(animalRepository).save(a);

        // Dos notificaciones
        verify(notificacionService, times(2)).enviar(anyString(), any(User.class));

        // DTO
        assertEquals(RequestStatus.APPROVED, result.getStatus());
    }

    @Test
    @DisplayName("aprobarSolicitud: notificacion al adoptante contiene Felicitaciones")
    void aprobarSolicitud_notificacionAdopanteContienefelicitaciones() throws Exception {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));
        when(solicitudRepository.save(s)).thenReturn(s);

        solicitudService.aprobarSolicitud(100L);

        verify(notificacionService).enviar(
                contains("Felicitaciones"),
                eq(adopter)
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    // rechazarSolicitud()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("rechazarSolicitud: solicitud no existe → SolicitudNoEncontradaException")
    void rechazarSolicitud_noExiste_lanzaExcepcion() {
        when(solicitudRepository.existsById(100L)).thenReturn(false);
        assertThrows(SolicitudNoEncontradaException.class,
                () -> solicitudService.rechazarSolicitud(100L, "Sin espacio"));
    }

    @Test
    @DisplayName("rechazarSolicitud: no esta pendiente → SolicitudNoPendienteException")
    void rechazarSolicitud_noPendiente_lanzaExcepcion() {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);
        s.setStatus(RequestStatus.REJECTED);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));

        assertThrows(SolicitudNoPendienteException.class,
                () -> solicitudService.rechazarSolicitud(100L, "Sin espacio"));
    }

    @Test
    @DisplayName("rechazarSolicitud: pendiente → REJECTED, animal vuelve a AVAILABLE, 2 notifs")
    void rechazarSolicitud_pendiente_rechaza() throws Exception {
        Animal a = animalDisponible();
        a.setStatus(AnimalStatus.PENDING); // estaba en pending
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));
        when(solicitudRepository.save(s)).thenReturn(s);

        SolicitudAdopcionDTO result = solicitudService.rechazarSolicitud(100L, "Sin espacio");

        // Estado solicitud
        assertEquals(RequestStatus.REJECTED, s.getStatus());
        assertEquals("Sin espacio", s.getRejectionReason());
        assertNotNull(s.getResolutionDate());

        // Animal vuelve a AVAILABLE
        assertEquals(AnimalStatus.AVAILABLE, a.getStatus());
        verify(animalRepository).save(a);

        // Dos notificaciones
        verify(notificacionService, times(2)).enviar(anyString(), any(User.class));

        assertEquals(RequestStatus.REJECTED, result.getStatus());
    }

    @Test
    @DisplayName("rechazarSolicitud: motivo incluido en notificacion al adoptante")
    void rechazarSolicitud_motivoEnNotificacion() throws Exception {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));
        when(solicitudRepository.save(s)).thenReturn(s);

        solicitudService.rechazarSolicitud(100L, "Sin espacio");

        verify(notificacionService).enviar(
                contains("Sin espacio"),
                eq(adopter)
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerPendientes()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("obtenerPendientes: con pendientes → lista DTOs")
    void obtenerPendientes_conPendientes_retornaLista() {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.findByStatus(RequestStatus.PENDING)).thenReturn(List.of(s));

        List<SolicitudAdopcionDTO> result = solicitudService.obtenerPendientes();

        assertEquals(1, result.size());
        assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
        assertEquals("Firulais", result.get(0).getAnimalName());
    }

    @Test
    @DisplayName("obtenerPendientes: sin pendientes → lista vacia")
    void obtenerPendientes_sinPendientes_listaVacia() {
        when(solicitudRepository.findByStatus(RequestStatus.PENDING)).thenReturn(List.of());
        assertTrue(solicitudService.obtenerPendientes().isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerPorAdoptante()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("obtenerPorAdoptante: con solicitudes → lista correcta")
    void obtenerPorAdoptante_conSolicitudes_retornaLista() {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.findByAdopterId(2L)).thenReturn(List.of(s));

        List<SolicitudAdopcionDTO> result = solicitudService.obtenerPorAdoptante(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getAdopterId());
    }

    @Test
    @DisplayName("obtenerPorAdoptante: sin solicitudes → lista vacia")
    void obtenerPorAdoptante_sinSolicitudes_listaVacia() {
        when(solicitudRepository.findByAdopterId(99L)).thenReturn(List.of());
        assertTrue(solicitudService.obtenerPorAdoptante(99L).isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerPorAnimal()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("obtenerPorAnimal: con solicitudes → lista correcta")
    void obtenerPorAnimal_conSolicitudes_retornaLista() {
        Animal a = animalDisponible();
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.findByAnimalId(10L)).thenReturn(List.of(s));

        List<SolicitudAdopcionDTO> result = solicitudService.obtenerPorAnimal(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getAnimalId());
    }

    @Test
    @DisplayName("obtenerPorAnimal: sin solicitudes → lista vacia")
    void obtenerPorAnimal_sinSolicitudes_listaVacia() {
        when(solicitudRepository.findByAnimalId(99L)).thenReturn(List.of());
        assertTrue(solicitudService.obtenerPorAnimal(99L).isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerIdPorUsername()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("obtenerIdPorUsername: usuario existe → retorna id")
    void obtenerIdPorUsername_existe_retornaId() throws UserNotFoundException {
        User adopter = adopter(); // id=2
        when(userRepository.existsByUsername("adoptante")).thenReturn(true);
        when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adopter));

        assertEquals(2L, solicitudService.obtenerIdPorUsername("adoptante"));
    }

    @Test
    @DisplayName("obtenerIdPorUsername: usuario no existe → UserNotFoundException")
    void obtenerIdPorUsername_noExiste_lanzaExcepcion() {
        when(userRepository.existsByUsername("nadie")).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> solicitudService.obtenerIdPorUsername("nadie"));
    }
}