package co.edu.unbosque.centroadoptivo;

import co.edu.unbosque.centroadoptivo.dto.AnimalDTO;
import co.edu.unbosque.centroadoptivo.dto.ResultadoIADTO;
import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalAge;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalClassification;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import co.edu.unbosque.centroadoptivo.entity.ResultadoIA;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.entity.ValidacionIA;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.ImagenException;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.ObservacionesException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;
import co.edu.unbosque.centroadoptivo.repository.AnimalRepository;
import co.edu.unbosque.centroadoptivo.repository.ResultadoIARepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.repository.ValidacionIARepository;
import co.edu.unbosque.centroadoptivo.service.AnimalService;
import co.edu.unbosque.centroadoptivo.service.IAOrquestadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock private AnimalRepository animalRepository;
    @Mock private UserRepository userRepository;
    @Mock private ValidacionIARepository validacionIARepository;
    @Mock private ResultadoIARepository resultadoIARepository;
    @Mock private IAOrquestadorService iaOrquestadorService;

    @InjectMocks private AnimalService animalService;

    // Directorio temporal real en disco para que guardarImagen() no falle
    @TempDir
    Path tempDir;

    // ─── Helpers globales ────────────────────────────────────────────────────

    /** Bytes mínimos de un JPEG 1×1 px válido */
    private static final byte[] JPEG_BYTES = new byte[]{
        (byte)0xFF,(byte)0xD8,(byte)0xFF,(byte)0xE0,0x00,0x10,0x4A,0x46,
        0x49,0x46,0x00,0x01,0x01,0x00,0x00,0x01,0x00,0x01,0x00,0x00,
        (byte)0xFF,(byte)0xDB,0x00,0x43,0x00,0x08,0x06,0x06,0x07,0x06,
        0x05,0x08,0x07,0x07,0x07,0x09,0x09,0x08,0x0A,0x0C,0x14,0x0D,
        0x0C,0x0B,0x0B,0x0C,0x19,0x12,0x13,0x0F,0x14,0x1D,0x1A,0x1F,
        0x1E,0x1D,0x1A,0x1C,0x1C,0x20,0x24,0x2E,0x27,0x20,0x22,0x2C,
        0x23,0x1C,0x1C,0x28,0x37,0x29,0x2C,0x30,0x31,0x34,0x34,0x34,
        0x1F,0x27,0x39,0x3D,0x38,0x32,0x3C,0x2E,0x33,0x34,0x32,
        (byte)0xFF,(byte)0xD9
    };

    private MockMultipartFile imagenJpegValida() {
        return new MockMultipartFile(
                "imagen", "firulais.jpg", "image/jpeg", JPEG_BYTES);
    }

    private MockMultipartFile imagenPngValida() {
        return new MockMultipartFile(
                "imagen", "firulais.png", "image/png",
                new byte[]{(byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A,
                           0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52});
    }

    private User samplePublisher() {
        User u = new User();
        u.setId(1L);
        u.setUsername("publicador");
        return u;
    }

    private Animal sampleAnimal(User publisher) {
        Animal a = new Animal();
        a.setId(10L);
        a.setName("Firulais");
        a.setSpecies("perro");
        a.setBreed("Labrador");
        a.setColor("amarillo");
        a.setAge(AnimalAge.ADULT);
        a.setClassification(AnimalClassification.DOMESTIC);
        a.setStatus(AnimalStatus.AVAILABLE);
        a.setObservations("Animal sano y jugueton, le gustan los ninos");
        a.setImage("/uploads/animales/firulais.jpg");
        a.setPublishedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());
        a.setPublisher(publisher);
        a.setAdopter(null);
        return a;
    }

    private AnimalDTO sampleDTO() {
        AnimalDTO dto = new AnimalDTO();
        dto.setName("Firulais");
        dto.setObservations("Animal sano y jugueton, le gustan los ninos");
        dto.setSterilized(true);
        dto.setVaccinated(true);
        return dto;
    }

    private ResultadoIADTO iaAprobado() {
        ResultadoIADTO r = new ResultadoIADTO();
        r.setEspecie("perro");
        r.setRaza("Labrador");
        r.setColor("amarillo");
        r.setEdad("ADULT");
        r.setClasificacion("DOMESTIC");
        r.setAprobado(true);
        r.setVotos(4);
        r.setTotalIAs(5);
        r.setDetalle("Todo OK");
        return r;
    }

    private ResultadoIADTO iaRechazado() {
        ResultadoIADTO r = new ResultadoIADTO();
        r.setAprobado(false);
        r.setVotos(1);
        r.setTotalIAs(5);
        r.setDetalle("No es un animal domestico");
        return r;
    }

    @BeforeEach
    void configurarDirectorio() {
        // Inyectamos el directorio temporal para que guardarImagen() escriba ahí
        ReflectionTestUtils.setField(
                animalService, "directorioImagenes", tempDir.toString());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // registrarAnimal() — validaciones de entrada
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("registrarAnimal() — validaciones de entrada")
    class RegistrarValidaciones {

        @Test
        @DisplayName("nombre nulo → NombreException")
        void nombre_nulo_lanzaNombreException() {
            AnimalDTO dto = sampleDTO();
            dto.setName(null);
            assertThrows(NombreException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
            verifyNoInteractions(animalRepository, iaOrquestadorService);
        }

        @Test
        @DisplayName("nombre vacio → NombreException")
        void nombre_vacio_lanzaNombreException() {
            AnimalDTO dto = sampleDTO();
            dto.setName("  ");
            assertThrows(NombreException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
        }

        @Test
        @DisplayName("nombre de 1 caracter → NombreException")
        void nombre_unChar_lanzaNombreException() {
            AnimalDTO dto = sampleDTO();
            dto.setName("A");
            assertThrows(NombreException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
        }

        @Test
        @DisplayName("nombre con numeros → NombreException")
        void nombre_conNumeros_lanzaNombreException() {
            AnimalDTO dto = sampleDTO();
            dto.setName("Firu123");
            assertThrows(NombreException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
        }

        @Test
        @DisplayName("observaciones nulas → ObservacionesException")
        void observaciones_nulas_lanzaObservacionesException() {
            AnimalDTO dto = sampleDTO();
            dto.setObservations(null);
            assertThrows(ObservacionesException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
        }

        @Test
        @DisplayName("observaciones menores de 10 chars → ObservacionesException")
        void observaciones_muyCortas_lanzaObservacionesException() {
            AnimalDTO dto = sampleDTO();
            dto.setObservations("Cortas");
            assertThrows(ObservacionesException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
        }

        @Test
        @DisplayName("observaciones mayores de 500 chars → ObservacionesException")
        void observaciones_muyLargas_lanzaObservacionesException() {
            AnimalDTO dto = sampleDTO();
            dto.setObservations("A".repeat(501));
            assertThrows(ObservacionesException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));
        }

        @Test
        @DisplayName("imagen nula → ImagenException")
        void imagen_nula_lanzaImagenException() {
            AnimalDTO dto = sampleDTO();
            assertThrows(ImagenException.class,
                    () -> animalService.registrarAnimal(dto, null, "publicador"));
        }

        @Test
        @DisplayName("imagen vacia → ImagenException")
        void imagen_vacia_lanzaImagenException() {
            AnimalDTO dto = sampleDTO();
            MockMultipartFile vacia = new MockMultipartFile(
                    "imagen", "vacia.jpg", "image/jpeg", new byte[0]);
            assertThrows(ImagenException.class,
                    () -> animalService.registrarAnimal(dto, vacia, "publicador"));
        }

        @Test
        @DisplayName("imagen tipo PDF → ImagenException")
        void imagen_tipoPdf_lanzaImagenException() {
            AnimalDTO dto = sampleDTO();
            MockMultipartFile pdf = new MockMultipartFile(
                    "imagen", "doc.pdf", "application/pdf", JPEG_BYTES);
            assertThrows(ImagenException.class,
                    () -> animalService.registrarAnimal(dto, pdf, "publicador"));
        }

        @Test
        @DisplayName("usuario no existe → UserNotFoundException")
        void usuario_noExiste_lanzaUserNotFoundException() {
            AnimalDTO dto = sampleDTO();
            when(userRepository.existsByUsername("fantasma")).thenReturn(false);
            assertThrows(UserNotFoundException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "fantasma"));
            verifyNoInteractions(animalRepository, iaOrquestadorService);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // registrarAnimal() — lógica de IA
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("registrarAnimal() — lógica de IA")
    class RegistrarIA {

        @Test
        @DisplayName("IA rechaza → ValidacionIAException, no se guarda nada")
        void ia_rechaza_lanzaExcepcionYNadaSeGuarda() {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaRechazado());

            assertThrows(ValidacionIAException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));

            verify(animalRepository, never()).save(any());
            verify(resultadoIARepository, never()).save(any());
            verify(validacionIARepository, never()).save(any());
        }

        @Test
        @DisplayName("IA rechaza → mensaje de excepcion incluye el detalle del resultado")
        void ia_rechaza_mensajeContieneDetalle() {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaRechazado());

            ValidacionIAException ex = assertThrows(ValidacionIAException.class,
                    () -> animalService.registrarAnimal(dto, imagenJpegValida(), "publicador"));

            assertTrue(ex.getMessage().contains("No es un animal domestico")
                    || ex.getMessage().contains("validación"));
        }

        @Test
        @DisplayName("IA aprueba → guarda animal, resultadoIA y validacionIA (3 saves)")
        void ia_aprueba_guardaTresEntidades() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            Animal saved = sampleAnimal(publisher);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(any(Animal.class))).thenReturn(saved);

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            verify(animalRepository).save(any(Animal.class));
            verify(resultadoIARepository).save(any(ResultadoIA.class));
            verify(validacionIARepository).save(any(ValidacionIA.class));
        }

        @Test
        @DisplayName("IA aprueba → especie, raza, color y edad copiados al animal")
        void ia_aprueba_datosIACopiadosAlAnimal() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            Animal guardado = captor.getValue();
            assertEquals("perro", guardado.getSpecies());
            assertEquals("Labrador", guardado.getBreed());
            assertEquals("amarillo", guardado.getColor());
            assertEquals(AnimalAge.ADULT, guardado.getAge());
            assertEquals(AnimalClassification.DOMESTIC, guardado.getClassification());
        }

        @Test
        @DisplayName("IA aprueba → estado AVAILABLE, adopter null, publisher y fechas asignados")
        void ia_aprueba_estadoInicialCorrecto() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            Animal guardado = captor.getValue();
            assertEquals(AnimalStatus.AVAILABLE, guardado.getStatus());
            assertNull(guardado.getAdopter());
            assertEquals(publisher, guardado.getPublisher());
            assertNotNull(guardado.getPublishedAt());
            assertNotNull(guardado.getUpdatedAt());
        }

        @Test
        @DisplayName("IA aprueba → edad con valor invalido para enum usa ADULT por defecto")
        void ia_aprueba_edadInvalidaUsaAdultPorDefecto() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            ResultadoIADTO iaEdadRara = iaAprobado();
            iaEdadRara.setEdad("CACHORRO_GIGANTE"); // no es valor del enum
            ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaEdadRara);
            when(animalRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            assertEquals(AnimalAge.ADULT, captor.getValue().getAge());
        }

        @Test
        @DisplayName("IA aprueba → clasificacion invalida para enum usa DOMESTIC por defecto")
        void ia_aprueba_clasificacionInvalidaUsaDomesticPorDefecto() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            ResultadoIADTO iaClasifRara = iaAprobado();
            iaClasifRara.setClasificacion("EXTRATERRESTRE"); // no existe en el enum
            ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaClasifRara);
            when(animalRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            assertEquals(AnimalClassification.DOMESTIC, captor.getValue().getClassification());
        }

        @Test
        @DisplayName("IA aprueba con PNG → funciona igual que JPEG")
        void ia_aprueba_conPng_funciona() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            Animal saved = sampleAnimal(publisher);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(any(Animal.class))).thenReturn(saved);

            AnimalDTO result = animalService.registrarAnimal(dto, imagenPngValida(), "publicador");

            assertNotNull(result);
            verify(animalRepository).save(any(Animal.class));
        }

        @Test
        @DisplayName("IA aprueba → DTO retornado contiene todos los campos correctos")
        void ia_aprueba_dtoRetornadoCorrecto() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            Animal saved = sampleAnimal(publisher);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(any(Animal.class))).thenReturn(saved);

            AnimalDTO result = animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals("Firulais", result.getName());
            assertEquals("perro", result.getSpecies());
            assertEquals("Labrador", result.getBreed());
            assertEquals(AnimalStatus.AVAILABLE, result.getStatus());
            assertEquals(1L, result.getPublisherId());
            assertNull(result.getAdopterId());
        }

        @Test
        @DisplayName("IA aprueba → ResultadoIA guardado con datos de IA y referencia al animal")
        void ia_aprueba_resultadoIAGuardadoCorrecto() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            Animal saved = sampleAnimal(publisher);
            ArgumentCaptor<ResultadoIA> captor = ArgumentCaptor.forClass(ResultadoIA.class);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(any(Animal.class))).thenReturn(saved);

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            verify(resultadoIARepository).save(captor.capture());
            ResultadoIA guardado = captor.getValue();
            assertEquals("perro", guardado.getEspecie());
            assertEquals("Labrador", guardado.getRaza());
            assertTrue(guardado.isAprobado());
            assertEquals(4, guardado.getVotos());
            assertEquals(5, guardado.getTotalIAs());
            assertEquals(saved, guardado.getAnimal());
            assertNotNull(guardado.getFechaDeteccion());
        }

        @Test
        @DisplayName("IA aprueba → ValidacionIA guardada con datos y referencia al animal")
        void ia_aprueba_validacionIAGuardadaCorrectamente() throws Exception {
            AnimalDTO dto = sampleDTO();
            User publisher = samplePublisher();
            Animal saved = sampleAnimal(publisher);
            ArgumentCaptor<ValidacionIA> captor = ArgumentCaptor.forClass(ValidacionIA.class);
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(publisher));
            when(iaOrquestadorService.detectarYValidar(anyString(), any(byte[].class), anyString()))
                    .thenReturn(iaAprobado());
            when(animalRepository.save(any(Animal.class))).thenReturn(saved);

            animalService.registrarAnimal(dto, imagenJpegValida(), "publicador");

            verify(validacionIARepository).save(captor.capture());
            ValidacionIA guardada = captor.getValue();
            assertTrue(guardada.isAprobado());
            assertEquals(4, guardada.getVotos());
            assertEquals(5, guardada.getTotalIAs());
            assertEquals(saved, guardada.getAnimal());
            assertNotNull(guardada.getFechaValidacion());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerAnimalesDisponibles()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerAnimalesDisponibles()")
    class ObtenerDisponibles {

        @Test
        @DisplayName("con animales AVAILABLE → lista de DTOs correcta")
        void conAnimales_retornaLista() {
            User pub = samplePublisher();
            Animal a = sampleAnimal(pub);
            when(animalRepository.findByStatus(AnimalStatus.AVAILABLE)).thenReturn(List.of(a));

            List<AnimalDTO> result = animalService.obtenerAnimalesDisponibles();

            assertEquals(1, result.size());
            assertEquals("Firulais", result.get(0).getName());
            assertEquals(AnimalStatus.AVAILABLE, result.get(0).getStatus());
        }

        @Test
        @DisplayName("sin animales AVAILABLE → lista vacia")
        void sinAnimales_listaVacia() {
            when(animalRepository.findByStatus(AnimalStatus.AVAILABLE)).thenReturn(List.of());
            assertTrue(animalService.obtenerAnimalesDisponibles().isEmpty());
        }

        @Test
        @DisplayName("solo consulta AVAILABLE, nunca ADOPTED ni PENDING")
        void consultaSoloAvailable() {
            when(animalRepository.findByStatus(AnimalStatus.AVAILABLE)).thenReturn(List.of());
            animalService.obtenerAnimalesDisponibles();
            verify(animalRepository).findByStatus(AnimalStatus.AVAILABLE);
            verify(animalRepository, never()).findByStatus(AnimalStatus.ADOPTED);
            verify(animalRepository, never()).findByStatus(AnimalStatus.PENDING);
        }

        @Test
        @DisplayName("varios animales → todos mapeados con publisherId correcto")
        void variosAnimales_todosMapeados() {
            User pub = samplePublisher();
            Animal a1 = sampleAnimal(pub);
            Animal a2 = sampleAnimal(pub);
            a2.setId(11L);
            a2.setName("Luna");
            when(animalRepository.findByStatus(AnimalStatus.AVAILABLE)).thenReturn(List.of(a1, a2));

            List<AnimalDTO> result = animalService.obtenerAnimalesDisponibles();

            assertEquals(2, result.size());
            assertTrue(result.stream().allMatch(d -> d.getPublisherId().equals(1L)));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerTodos()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerTodos()")
    class ObtenerTodos {

        @Test
        @DisplayName("con animales → lista completa de DTOs")
        void conAnimales_retornaLista() {
            User pub = samplePublisher();
            when(animalRepository.findAll()).thenReturn(List.of(sampleAnimal(pub)));
            assertEquals(1, animalService.obtenerTodos().size());
        }

        @Test
        @DisplayName("sin animales → lista vacia")
        void sinAnimales_listaVacia() {
            when(animalRepository.findAll()).thenReturn(List.of());
            assertTrue(animalService.obtenerTodos().isEmpty());
        }

        @Test
        @DisplayName("mezcla AVAILABLE + PENDING + ADOPTED → todos incluidos")
        void mezlaEstados_todosIncluidos() {
            User pub = samplePublisher();
            Animal a1 = sampleAnimal(pub);
            Animal a2 = sampleAnimal(pub); a2.setStatus(AnimalStatus.PENDING);
            Animal a3 = sampleAnimal(pub); a3.setStatus(AnimalStatus.ADOPTED);
            when(animalRepository.findAll()).thenReturn(List.of(a1, a2, a3));
            assertEquals(3, animalService.obtenerTodos().size());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerAnimalPorId()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerAnimalPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("animal existe → DTO con todos los campos")
        void existe_retornaDTO() throws AnimalNoEncontradoException {
            User pub = samplePublisher();
            Animal a = sampleAnimal(pub);
            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(a));

            AnimalDTO result = animalService.obtenerAnimalPorId(10L);

            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals("Firulais", result.getName());
            assertEquals("perro", result.getSpecies());
            assertEquals(1L, result.getPublisherId());
        }

        @Test
        @DisplayName("animal no existe → AnimalNoEncontradoException")
        void noExiste_lanzaExcepcion() {
            when(animalRepository.existsById(99L)).thenReturn(false);
            assertThrows(AnimalNoEncontradoException.class,
                    () -> animalService.obtenerAnimalPorId(99L));
        }

        @Test
        @DisplayName("animal con adopter → adopterId presente en DTO")
        void conAdopter_adopterIdPresente() throws AnimalNoEncontradoException {
            User pub = samplePublisher();
            User adopter = new User(); adopter.setId(5L);
            Animal a = sampleAnimal(pub);
            a.setAdopter(adopter);
            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(a));

            assertEquals(5L, animalService.obtenerAnimalPorId(10L).getAdopterId());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerAnimalesPorPublicador()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerAnimalesPorPublicador()")
    class ObtenerPorPublicador {

        @Test
        @DisplayName("publicador con animales → lista correcta")
        void conAnimales_retornaLista() {
            User pub = samplePublisher();
            when(animalRepository.findByPublisherId(1L)).thenReturn(List.of(sampleAnimal(pub)));

            List<AnimalDTO> result = animalService.obtenerAnimalesPorPublicador(1L);

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getPublisherId());
        }

        @Test
        @DisplayName("publicador sin animales → lista vacia")
        void sinAnimales_listaVacia() {
            when(animalRepository.findByPublisherId(99L)).thenReturn(List.of());
            assertTrue(animalService.obtenerAnimalesPorPublicador(99L).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // actualizarAnimal()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("actualizarAnimal()")
    class ActualizarAnimal {

        @Test
        @DisplayName("animal no existe → AnimalNoEncontradoException")
        void noExiste_lanzaExcepcion() {
            when(animalRepository.existsById(99L)).thenReturn(false);
            assertThrows(AnimalNoEncontradoException.class,
                    () -> animalService.actualizarAnimal(99L, sampleDTO()));
        }

        @Test
        @DisplayName("nombre con numeros → NombreException")
        void nombreInvalido_lanzaExcepcion() {
            when(animalRepository.existsById(10L)).thenReturn(true);
            AnimalDTO dto = sampleDTO();
            dto.setName("Firu123");
            assertThrows(NombreException.class,
                    () -> animalService.actualizarAnimal(10L, dto));
        }

        @Test
        @DisplayName("observaciones muy cortas → ObservacionesException")
        void observacionesCortas_lanzaExcepcion() {
            when(animalRepository.existsById(10L)).thenReturn(true);
            AnimalDTO dto = sampleDTO();
            dto.setObservations("Corto");
            assertThrows(ObservacionesException.class,
                    () -> animalService.actualizarAnimal(10L, dto));
        }

        @Test
        @DisplayName("datos validos → actualiza nombre, observaciones, esterilizado, vacunado")
        void datosValidos_actualizaCamposEditables() throws Exception {
            User pub = samplePublisher();
            Animal existing = sampleAnimal(pub);
            AnimalDTO dto = sampleDTO();
            dto.setName("Princesa");
            dto.setObservations("Animal muy tranquila y amigable con todos");
            dto.setSterilized(false);
            dto.setVaccinated(false);

            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(existing));
            when(animalRepository.save(existing)).thenReturn(existing);

            animalService.actualizarAnimal(10L, dto);

            assertEquals("Princesa", existing.getName());
            assertEquals("Animal muy tranquila y amigable con todos", existing.getObservations());
            assertFalse(existing.isSterilized());
            assertFalse(existing.isVaccinated());
            assertNotNull(existing.getUpdatedAt());
            verify(animalRepository).save(existing);
        }

        @Test
        @DisplayName("actualizacion NO toca especie, raza ni color (datos de IA se preservan)")
        void actualizacion_preservaDatosIA() throws Exception {
            User pub = samplePublisher();
            Animal existing = sampleAnimal(pub);
            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(existing));
            when(animalRepository.save(existing)).thenReturn(existing);

            animalService.actualizarAnimal(10L, sampleDTO());

            assertEquals("perro", existing.getSpecies());
            assertEquals("Labrador", existing.getBreed());
            assertEquals("amarillo", existing.getColor());
        }

        @Test
        @DisplayName("actualizacion → retorna DTO actualizado, no null")
        void actualizacion_retornaDTO() throws Exception {
            User pub = samplePublisher();
            Animal existing = sampleAnimal(pub);
            when(animalRepository.existsById(10L)).thenReturn(true);
            when(animalRepository.findById(10L)).thenReturn(Optional.of(existing));
            when(animalRepository.save(existing)).thenReturn(existing);

            AnimalDTO result = animalService.actualizarAnimal(10L, sampleDTO());

            assertNotNull(result);
            assertEquals(10L, result.getId());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // eliminarAnimal()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("eliminarAnimal()")
    class EliminarAnimal {

        @Test
        @DisplayName("animal existe → llama deleteById correctamente")
        void existe_eliminaCorrectamente() throws AnimalNoEncontradoException {
            when(animalRepository.existsById(10L)).thenReturn(true);
            animalService.eliminarAnimal(10L);
            verify(animalRepository).deleteById(10L);
        }

        @Test
        @DisplayName("animal no existe → AnimalNoEncontradoException, sin deleteById")
        void noExiste_lanzaExcepcionSinEliminar() {
            when(animalRepository.existsById(99L)).thenReturn(false);
            assertThrows(AnimalNoEncontradoException.class,
                    () -> animalService.eliminarAnimal(99L));
            verify(animalRepository, never()).deleteById(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // obtenerIdPorUsername()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerIdPorUsername()")
    class ObtenerIdPorUsername {

        @Test
        @DisplayName("usuario existe → retorna id correcto")
        void existe_retornaId() throws UserNotFoundException {
            User pub = samplePublisher(); // id=1
            when(userRepository.existsByUsername("publicador")).thenReturn(true);
            when(userRepository.findByUsername("publicador")).thenReturn(Optional.of(pub));
            assertEquals(1L, animalService.obtenerIdPorUsername("publicador"));
        }

        @Test
        @DisplayName("usuario no existe → UserNotFoundException")
        void noExiste_lanzaExcepcion() {
            when(userRepository.existsByUsername("nadie")).thenReturn(false);
            assertThrows(UserNotFoundException.class,
                    () -> animalService.obtenerIdPorUsername("nadie"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // findById()
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("animal existe → Optional con animal")
        void existe_retornaOptional() {
            User pub = samplePublisher();
            when(animalRepository.findById(10L)).thenReturn(Optional.of(sampleAnimal(pub)));
            Optional<Animal> result = animalService.findById(10L);
            assertTrue(result.isPresent());
            assertEquals(10L, result.get().getId());
        }

        @Test
        @DisplayName("animal no existe → Optional vacio")
        void noExiste_retornaOptionalVacio() {
            when(animalRepository.findById(99L)).thenReturn(Optional.empty());
            assertTrue(animalService.findById(99L).isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Mapeo DTO — campos opcionales publisher / adopter
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Mapeo DTO — publisher y adopter")
    class MapeoCamposOpcionales {

        @Test
        @DisplayName("animal sin adopter → adopterId es null en DTO")
        void sinAdopter_adopterIdNull() {
            User pub = samplePublisher();
            Animal a = sampleAnimal(pub);
            a.setAdopter(null);
            when(animalRepository.findByStatus(AnimalStatus.AVAILABLE)).thenReturn(List.of(a));

            AnimalDTO dto = animalService.obtenerAnimalesDisponibles().get(0);
            assertNull(dto.getAdopterId());
        }

        @Test
        @DisplayName("animal con adopter → adopterId correcto en DTO")
        void conAdopter_adopterIdPresente() {
            User pub = samplePublisher();
            User adopter = new User(); adopter.setId(7L);
            Animal a = sampleAnimal(pub);
            a.setAdopter(adopter);
            a.setStatus(AnimalStatus.ADOPTED);
            when(animalRepository.findAll()).thenReturn(List.of(a));

            AnimalDTO dto = animalService.obtenerTodos().get(0);
            assertEquals(7L, dto.getAdopterId());
        }
    }
}