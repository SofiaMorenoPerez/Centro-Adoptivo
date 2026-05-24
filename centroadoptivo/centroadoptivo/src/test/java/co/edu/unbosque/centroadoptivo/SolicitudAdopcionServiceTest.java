package co.edu.unbosque.centroadoptivo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitudAdopcionService — Pruebas unitarias")
class SolicitudAdopcionServiceTest {

    @Mock private SolicitudAdopcionRepository solicitudRepository;
    @Mock private AnimalRepository animalRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificacionService notificacionService;

    @InjectMocks
    private SolicitudAdopcionService solicitudService;

    // ── Helpers ───────────────────────────────────────────────
    private User crearPublicador() {
        User u = new User();
        u.setId(1L);
        u.setUsername("publicador");
        return u;
    }

    private User crearAdoptante() {
        User u = new User();
        u.setId(2L);
        u.setUsername("adoptante");
        return u;
    }

    private Animal crearAnimalDisponible(User publicador) {
        Animal a = new Animal();
        a.setId(10L);
        a.setName("Firulais");
        a.setStatus(AnimalStatus.AVAILABLE);
        a.setPublisher(publicador);
        return a;
    }

    private SolicitudAdopcion crearSolicitudPendiente(Animal animal, User adoptante) {
        SolicitudAdopcion s = new SolicitudAdopcion();
        s.setId(100L);
        s.setAnimal(animal);
        s.setAdopter(adoptante);
        s.setStatus(RequestStatus.PENDING);
        s.setRequestDate(LocalDateTime.now());
        return s;
    }

    // ═══════════════════════════════════════════════════════════
    // CREAR SOLICITUD
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("crearSolicitud()")
    class CrearSolicitudTest {

        @Test
        @DisplayName("Debe crear solicitud correctamente")
        void crear_exitoso() throws Exception {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);

            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
            when(userRepository.existsByUsername("adoptante")).thenReturn(true);
            when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adoptante));
            when(solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                    10L, 2L, RequestStatus.PENDING)).thenReturn(false);
            when(animalRepository.save(any())).thenReturn(animal);
            when(solicitudRepository.save(any())).thenReturn(solicitud);

            SolicitudAdopcionDTO resultado = solicitudService.crearSolicitud(10L, "adoptante");

            assertNotNull(resultado);
            assertEquals(10L, resultado.getAnimalId());
            assertEquals("Firulais", resultado.getAnimalName());
            verify(notificacionService, times(2)).enviar(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Debe lanzar AnimalNoEncontradoException si el animal no existe")
        void crear_animal_no_existe() {
            when(animalRepository.existsById(99L)).thenReturn(false);

            assertThrows(AnimalNoEncontradoException.class, () ->
                solicitudService.crearSolicitud(99L, "adoptante"));
        }

        @Test
        @DisplayName("Debe lanzar AnimalNoDisponibleException si el animal no está disponible")
        void crear_animal_no_disponible() {
            User publicador = crearPublicador();
            Animal animal = crearAnimalDisponible(publicador);
            animal.setStatus(AnimalStatus.PENDING); // no disponible

            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));

            assertThrows(AnimalNoDisponibleException.class, () ->
                solicitudService.crearSolicitud(10L, "adoptante"));
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si el adoptante no existe")
        void crear_adoptante_no_existe() {
            User publicador = crearPublicador();
            Animal animal = crearAnimalDisponible(publicador);

            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
            when(userRepository.existsByUsername("noexiste")).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                solicitudService.crearSolicitud(10L, "noexiste"));
        }

        @Test
        @DisplayName("Debe lanzar AnimalNoDisponibleException si el adoptante es el mismo publicador")
        void crear_publicador_no_puede_adoptar_su_propio_animal() {
            User publicador = crearPublicador(); // id=1
            Animal animal = crearAnimalDisponible(publicador);

            // El "adoptante" tiene el mismo id que el publicador
            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publicador));

            assertThrows(AnimalNoDisponibleException.class, () ->
                solicitudService.crearSolicitud(10L, "publicador"));
        }

        @Test
        @DisplayName("Debe lanzar SolicitudDuplicadaException si ya existe una solicitud pendiente")
        void crear_solicitud_duplicada() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);

            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
            when(userRepository.existsByUsername("adoptante")).thenReturn(true);
            when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adoptante));
            when(solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                    10L, 2L, RequestStatus.PENDING)).thenReturn(true);

            assertThrows(SolicitudDuplicadaException.class, () ->
                solicitudService.crearSolicitud(10L, "adoptante"));
        }

        @Test
        @DisplayName("Debe cambiar el estado del animal a PENDING al crear solicitud")
        void crear_cambia_estado_animal_a_pending() throws Exception {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);

            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
            when(userRepository.existsByUsername("adoptante")).thenReturn(true);
            when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adoptante));
            when(solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                    10L, 2L, RequestStatus.PENDING)).thenReturn(false);
            when(animalRepository.save(any())).thenReturn(animal);
            when(solicitudRepository.save(any())).thenReturn(solicitud);

            solicitudService.crearSolicitud(10L, "adoptante");

            assertEquals(AnimalStatus.PENDING, animal.getStatus());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // APROBAR SOLICITUD
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("aprobarSolicitud()")
    class AprobarSolicitudTest {

        @Test
        @DisplayName("Debe aprobar la solicitud correctamente")
        void aprobar_exitoso() throws Exception {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
            when(animalRepository.save(any())).thenReturn(animal);
            when(solicitudRepository.save(any())).thenReturn(solicitud);

            SolicitudAdopcionDTO resultado = solicitudService.aprobarSolicitud(100L);

            assertNotNull(resultado);
            assertEquals(RequestStatus.APPROVED, solicitud.getStatus());
            assertEquals(AnimalStatus.ADOPTED, animal.getStatus());
            assertEquals(adoptante, animal.getAdopter());
            verify(notificacionService, times(2)).enviar(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Debe lanzar SolicitudNoEncontradaException si la solicitud no existe")
        void aprobar_solicitud_no_existe() {
            when(solicitudRepository.existsById(99L)).thenReturn(false);

            assertThrows(SolicitudNoEncontradaException.class, () ->
                solicitudService.aprobarSolicitud(99L));
        }

        @Test
        @DisplayName("Debe lanzar SolicitudNoPendienteException si la solicitud ya fue aprobada")
        void aprobar_solicitud_ya_aprobada() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);
            solicitud.setStatus(RequestStatus.APPROVED); // ya aprobada

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));

            assertThrows(SolicitudNoPendienteException.class, () ->
                solicitudService.aprobarSolicitud(100L));
        }

        @Test
        @DisplayName("Debe lanzar SolicitudNoPendienteException si la solicitud fue rechazada")
        void aprobar_solicitud_ya_rechazada() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);
            solicitud.setStatus(RequestStatus.REJECTED);

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));

            assertThrows(SolicitudNoPendienteException.class, () ->
                solicitudService.aprobarSolicitud(100L));
        }

        @Test
        @DisplayName("Debe asignar fecha de resolución al aprobar")
        void aprobar_asigna_fecha_resolucion() throws Exception {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
            when(animalRepository.save(any())).thenReturn(animal);
            when(solicitudRepository.save(any())).thenReturn(solicitud);

            solicitudService.aprobarSolicitud(100L);

            assertNotNull(solicitud.getResolutionDate());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // RECHAZAR SOLICITUD
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("rechazarSolicitud()")
    class RechazarSolicitudTest {

        @Test
        @DisplayName("Debe rechazar la solicitud correctamente")
        void rechazar_exitoso() throws Exception {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
            when(animalRepository.save(any())).thenReturn(animal);
            when(solicitudRepository.save(any())).thenReturn(solicitud);

            SolicitudAdopcionDTO resultado = solicitudService.rechazarSolicitud(
                    100L, "No cumple requisitos");

            assertNotNull(resultado);
            assertEquals(RequestStatus.REJECTED, solicitud.getStatus());
            assertEquals("No cumple requisitos", solicitud.getRejectionReason());
            assertEquals(AnimalStatus.AVAILABLE, animal.getStatus()); // vuelve a disponible
            verify(notificacionService, times(2)).enviar(anyString(), any(User.class));
        }

        @Test
        @DisplayName("Debe lanzar SolicitudNoEncontradaException si no existe")
        void rechazar_solicitud_no_existe() {
            when(solicitudRepository.existsById(99L)).thenReturn(false);

            assertThrows(SolicitudNoEncontradaException.class, () ->
                solicitudService.rechazarSolicitud(99L, "razón"));
        }

        @Test
        @DisplayName("Debe lanzar SolicitudNoPendienteException si ya fue aprobada")
        void rechazar_solicitud_ya_aprobada() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);
            solicitud.setStatus(RequestStatus.APPROVED);

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));

            assertThrows(SolicitudNoPendienteException.class, () ->
                solicitudService.rechazarSolicitud(100L, "razón"));
        }

        @Test
        @DisplayName("Debe devolver el animal a AVAILABLE al rechazar")
        void rechazar_devuelve_animal_disponible() throws Exception {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            animal.setStatus(AnimalStatus.PENDING);
            SolicitudAdopcion solicitud = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.existsById(100L)).thenReturn(true);
            when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
            when(animalRepository.save(any())).thenReturn(animal);
            when(solicitudRepository.save(any())).thenReturn(solicitud);

            solicitudService.rechazarSolicitud(100L, "razón");

            assertEquals(AnimalStatus.AVAILABLE, animal.getStatus());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CONSULTAS
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Consultas (obtener)")
    class ConsultasTest {

        @Test
        @DisplayName("obtenerPendientes() debe retornar solo las solicitudes PENDING")
        void obtener_pendientes() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion s = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.findByStatus(RequestStatus.PENDING))
                .thenReturn(List.of(s));

            List<SolicitudAdopcionDTO> resultado = solicitudService.obtenerPendientes();

            assertEquals(1, resultado.size());
            assertEquals(RequestStatus.PENDING, resultado.get(0).getStatus());
        }

        @Test
        @DisplayName("obtenerPendientes() debe retornar lista vacía si no hay pendientes")
        void obtener_pendientes_vacio() {
            when(solicitudRepository.findByStatus(RequestStatus.PENDING))
                .thenReturn(List.of());

            List<SolicitudAdopcionDTO> resultado = solicitudService.obtenerPendientes();

            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("obtenerPorAdoptante() debe retornar solicitudes del adoptante")
        void obtener_por_adoptante() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion s = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.findByAdopterId(2L)).thenReturn(List.of(s));

            List<SolicitudAdopcionDTO> resultado = solicitudService.obtenerPorAdoptante(2L);

            assertEquals(1, resultado.size());
            assertEquals(2L, resultado.get(0).getAdopterId());
        }

        @Test
        @DisplayName("obtenerPorAnimal() debe retornar solicitudes del animal")
        void obtener_por_animal() {
            User publicador = crearPublicador();
            User adoptante = crearAdoptante();
            Animal animal = crearAnimalDisponible(publicador);
            SolicitudAdopcion s = crearSolicitudPendiente(animal, adoptante);

            when(solicitudRepository.findByAnimalId(10L)).thenReturn(List.of(s));

            List<SolicitudAdopcionDTO> resultado = solicitudService.obtenerPorAnimal(10L);

            assertEquals(1, resultado.size());
            assertEquals(10L, resultado.get(0).getAnimalId());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // OBTENER ID POR USERNAME
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("obtenerIdPorUsername()")
    class ObtenerIdPorUsernameTest {

        @Test
        @DisplayName("Debe retornar el ID del usuario si existe")
        void obtener_id_exitoso() throws Exception {
            User adoptante = crearAdoptante(); // id=2

            when(userRepository.existsByUsername("adoptante")).thenReturn(true);
            when(userRepository.findByUsername("adoptante")).thenReturn(Optional.of(adoptante));

            Long id = solicitudService.obtenerIdPorUsername("adoptante");

            assertEquals(2L, id);
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si el usuario no existe")
        void obtener_id_usuario_no_existe() {
            when(userRepository.existsByUsername("noexiste")).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                solicitudService.obtenerIdPorUsername("noexiste"));
        }
    }
}