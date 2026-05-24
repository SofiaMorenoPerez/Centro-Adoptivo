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

/**
 * Pruebas unitarias para {@link SolicitudAdopcionService}.
 * <p>
 * Verifica el comportamiento del servicio de solicitudes de adopción usando Mockito
 * para simular los repositorios y servicios dependientes, sin necesidad de levantar
 * el contexto de Spring.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class SolicitudAdopcionServiceTest {

    /** Mock del repositorio de solicitudes de adopción. */
    @Mock private SolicitudAdopcionRepository solicitudRepository;

    /** Mock del repositorio de animales. */
    @Mock private AnimalRepository animalRepository;

    /** Mock del repositorio de usuarios. */
    @Mock private UserRepository userRepository;

    /** Mock del servicio de notificaciones. */
    @Mock private NotificacionService notificacionService;

    /** Instancia del servicio bajo prueba con dependencias mockeadas. */
    @InjectMocks private SolicitudAdopcionService solicitudService;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Crea un usuario publicador de muestra con id {@code 1} y username {@code "publicador"}.
     *
     * @return instancia de {@link User} representando al publicador
     */
    private User publisher() {
        User u = new User();
        u.setId(1L);
        u.setUsername("publicador");
        return u;
    }

    /**
     * Crea un usuario adoptante de muestra con id {@code 2} y username {@code "adoptante"}.
     *
     * @return instancia de {@link User} representando al adoptante
     */
    private User adopter() {
        User u = new User();
        u.setId(2L);
        u.setUsername("adoptante");
        return u;
    }

    /**
     * Crea un animal disponible de muestra con id {@code 10}, nombre {@code "Firulais"}
     * y estado {@link AnimalStatus#AVAILABLE}, asociado al publicador.
     *
     * @return instancia de {@link Animal} disponible para adopción
     */
    private Animal animalDisponible() {
        Animal a = new Animal();
        a.setId(10L);
        a.setName("Firulais");
        a.setStatus(AnimalStatus.AVAILABLE);
        a.setPublisher(publisher());
        return a;
    }

    /**
     * Crea una solicitud de adopción pendiente de muestra con id {@code 100},
     * asociada al animal y adoptante dados.
     *
     * @param animal  animal sobre el que se realiza la solicitud
     * @param adopter usuario que realiza la solicitud
     * @return instancia de {@link SolicitudAdopcion} en estado {@link RequestStatus#PENDING}
     */
    private SolicitudAdopcion solicitudPendiente(Animal animal, User adopter) {
        SolicitudAdopcion s = new SolicitudAdopcion(animal, adopter,
                LocalDateTime.now(), RequestStatus.PENDING);
        s.setId(100L);
        return s;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // crearSolicitud()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code crearSolicitud} lanza {@link AnimalNoEncontradoException}
     * cuando el animal no existe en el repositorio.
     */
    @Test
    @DisplayName("crearSolicitud: animal no existe → AnimalNoEncontradoException")
    void crearSolicitud_animalNoExiste_lanzaExcepcion() {
        when(animalRepository.existsById(10L)).thenReturn(false);
        assertThrows(AnimalNoEncontradoException.class,
                () -> solicitudService.crearSolicitud(10L, "adoptante"));
    }

    /**
     * Verifica que {@code crearSolicitud} lanza {@link AnimalNoDisponibleException}
     * cuando el animal existe pero no está en estado {@link AnimalStatus#AVAILABLE}.
     */
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

    /**
     * Verifica que {@code crearSolicitud} lanza {@link UserNotFoundException}
     * cuando el usuario adoptante no existe en el repositorio.
     */
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

    /**
     * Verifica que {@code crearSolicitud} lanza {@link AnimalNoDisponibleException}
     * cuando el adoptante es el mismo usuario que publicó el animal.
     */
    @Test
    @DisplayName("crearSolicitud: adoptante es el mismo publicador → AnimalNoDisponibleException")
    void crearSolicitud_adoptanteEsPublicador_lanzaExcepcion() {
        User pub = publisher();
        Animal a = animalDisponible();

        when(animalRepository.existsById(10L)).thenReturn(true);
        when(animalRepository.findById(10L)).thenReturn(Optional.of(a));
        when(userRepository.existsByUsername("publicador")).thenReturn(true);
        when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(pub));

        assertThrows(AnimalNoDisponibleException.class,
                () -> solicitudService.crearSolicitud(10L, "publicador"));
    }

    /**
     * Verifica que {@code crearSolicitud} lanza {@link SolicitudDuplicadaException}
     * cuando ya existe una solicitud pendiente del mismo adoptante para el mismo animal.
     */
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

    /**
     * Verifica que {@code crearSolicitud} con datos válidos cambia el estado del animal
     * a {@link AnimalStatus#PENDING}, guarda la solicitud, envía dos notificaciones
     * y retorna un DTO correctamente formado.
     *
     * @throws Exception no se espera en este escenario
     */
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

        assertEquals(AnimalStatus.PENDING, a.getStatus());
        verify(animalRepository).save(a);
        verify(solicitudRepository).save(any(SolicitudAdopcion.class));
        verify(notificacionService, times(2)).enviar(anyString(), any(User.class));
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getAnimalId());
        assertEquals("Firulais", result.getAnimalName());
        assertEquals(2L, result.getAdopterId());
        assertEquals("adoptante", result.getAdopterUsername());
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    /**
     * Verifica que al crear una solicitud válida, la notificación enviada al publicador
     * contiene el nombre de usuario del adoptante.
     *
     * @throws Exception no se espera en este escenario
     */
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

    

    /**
     * Verifica que {@code aprobarSolicitud} lanza {@link SolicitudNoEncontradaException}
     * cuando la solicitud no existe en el repositorio.
     */
    @Test
    @DisplayName("aprobarSolicitud: solicitud no existe → SolicitudNoEncontradaException")
    void aprobarSolicitud_noExiste_lanzaExcepcion() {
        when(solicitudRepository.existsById(100L)).thenReturn(false);
        assertThrows(SolicitudNoEncontradaException.class,
                () -> solicitudService.aprobarSolicitud(100L));
    }

    /**
     * Verifica que {@code aprobarSolicitud} lanza {@link SolicitudNoPendienteException}
     * cuando la solicitud existe pero no está en estado {@link RequestStatus#PENDING}.
     */
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

    /**
     * Verifica que {@code aprobarSolicitud} con una solicitud pendiente cambia su estado
     * a {@link RequestStatus#APPROVED}, asigna fecha de resolución, cambia el animal a
     * {@link AnimalStatus#ADOPTED}, asigna el adoptante al animal y envía dos notificaciones.
     *
     * @throws Exception no se espera en este escenario
     */
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

        assertEquals(RequestStatus.APPROVED, s.getStatus());
        assertNotNull(s.getResolutionDate());
        assertEquals(AnimalStatus.ADOPTED, a.getStatus());
        assertEquals(adopter, a.getAdopter());
        verify(animalRepository).save(a);
        verify(notificacionService, times(2)).enviar(anyString(), any(User.class));
        assertEquals(RequestStatus.APPROVED, result.getStatus());
    }

    /**
     * Verifica que al aprobar una solicitud, la notificación enviada al adoptante
     * contiene la palabra {@code "Felicitaciones"}.
     *
     * @throws Exception no se espera en este escenario
     */
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

    

    /**
     * Verifica que {@code rechazarSolicitud} lanza {@link SolicitudNoEncontradaException}
     * cuando la solicitud no existe en el repositorio.
     */
    @Test
    @DisplayName("rechazarSolicitud: solicitud no existe → SolicitudNoEncontradaException")
    void rechazarSolicitud_noExiste_lanzaExcepcion() {
        when(solicitudRepository.existsById(100L)).thenReturn(false);
        assertThrows(SolicitudNoEncontradaException.class,
                () -> solicitudService.rechazarSolicitud(100L, "Sin espacio"));
    }

    /**
     * Verifica que {@code rechazarSolicitud} lanza {@link SolicitudNoPendienteException}
     * cuando la solicitud existe pero no está en estado {@link RequestStatus#PENDING}.
     */
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

    /**
     * Verifica que {@code rechazarSolicitud} con una solicitud pendiente cambia su estado
     * a {@link RequestStatus#REJECTED}, asigna el motivo de rechazo, la fecha de resolución,
     * devuelve el animal a {@link AnimalStatus#AVAILABLE} y envía dos notificaciones.
     *
     * @throws Exception no se espera en este escenario
     */
    @Test
    @DisplayName("rechazarSolicitud: pendiente → REJECTED, animal vuelve a AVAILABLE, 2 notifs")
    void rechazarSolicitud_pendiente_rechaza() throws Exception {
        Animal a = animalDisponible();
        a.setStatus(AnimalStatus.PENDING);
        User adopter = adopter();
        SolicitudAdopcion s = solicitudPendiente(a, adopter);

        when(solicitudRepository.existsById(100L)).thenReturn(true);
        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(s));
        when(solicitudRepository.save(s)).thenReturn(s);

        SolicitudAdopcionDTO result = solicitudService.rechazarSolicitud(100L, "Sin espacio");

        assertEquals(RequestStatus.REJECTED, s.getStatus());
        assertEquals("Sin espacio", s.getRejectionReason());
        assertNotNull(s.getResolutionDate());
        assertEquals(AnimalStatus.AVAILABLE, a.getStatus());
        verify(animalRepository).save(a);
        verify(notificacionService, times(2)).enviar(anyString(), any(User.class));
        assertEquals(RequestStatus.REJECTED, result.getStatus());
    }

    /**
     * Verifica que al rechazar una solicitud, la notificación enviada al adoptante
     * contiene el motivo de rechazo indicado.
     *
     * @throws Exception no se espera en este escenario
     */
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

    

    /**
     * Verifica que {@code obtenerPendientes} retorna la lista de DTOs correctamente
     * mapeados cuando existen solicitudes en estado {@link RequestStatus#PENDING}.
     */
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

    /**
     * Verifica que {@code obtenerPendientes} retorna una lista vacía
     * cuando no hay solicitudes pendientes.
     */
    @Test
    @DisplayName("obtenerPendientes: sin pendientes → lista vacia")
    void obtenerPendientes_sinPendientes_listaVacia() {
        when(solicitudRepository.findByStatus(RequestStatus.PENDING)).thenReturn(List.of());
        assertTrue(solicitudService.obtenerPendientes().isEmpty());
    }

   
    /**
     * Verifica que {@code obtenerPorAdoptante} retorna la lista de solicitudes
     * asociadas al adoptante indicado cuando existen registros.
     */
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

    /**
     * Verifica que {@code obtenerPorAdoptante} retorna una lista vacía
     * cuando el adoptante no tiene solicitudes registradas.
     */
    @Test
    @DisplayName("obtenerPorAdoptante: sin solicitudes → lista vacia")
    void obtenerPorAdoptante_sinSolicitudes_listaVacia() {
        when(solicitudRepository.findByAdopterId(99L)).thenReturn(List.of());
        assertTrue(solicitudService.obtenerPorAdoptante(99L).isEmpty());
    }

   

    /**
     * Verifica que {@code obtenerPorAnimal} retorna la lista de solicitudes
     * asociadas al animal indicado cuando existen registros.
     */
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

    /**
     * Verifica que {@code obtenerPorAnimal} retorna una lista vacía
     * cuando el animal no tiene solicitudes registradas.
     */
    @Test
    @DisplayName("obtenerPorAnimal: sin solicitudes → lista vacia")
    void obtenerPorAnimal_sinSolicitudes_listaVacia() {
        when(solicitudRepository.findByAnimalId(99L)).thenReturn(List.of());
        assertTrue(solicitudService.obtenerPorAnimal(99L).isEmpty());
    }


    /**
     * Verifica que {@code obtenerIdPorUsername} retorna el id del usuario
     * cuando el username existe en el repositorio.
     *
     * @throws UserNotFoundException no se espera en este escenario
     */
    @Test
    @DisplayName("obtenerIdPorUsername: usuario existe → retorna id")
    void obtenerIdPorUsername_existe_retornaId() throws UserNotFoundException {
        User adopter = adopter();
        when(userRepository.existsByUsername("adoptante")).thenReturn(true);
        when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adopter));

        assertEquals(2L, solicitudService.obtenerIdPorUsername("adoptante"));
    }

    /**
     * Verifica que {@code obtenerIdPorUsername} lanza {@link UserNotFoundException}
     * cuando el username no existe en el repositorio.
     */
    @Test
    @DisplayName("obtenerIdPorUsername: usuario no existe → UserNotFoundException")
    void obtenerIdPorUsername_noExiste_lanzaExcepcion() {
        when(userRepository.existsByUsername("nadie")).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> solicitudService.obtenerIdPorUsername("nadie"));
    }
}