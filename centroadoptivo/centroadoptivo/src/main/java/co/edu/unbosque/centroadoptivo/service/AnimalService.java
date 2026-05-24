package co.edu.unbosque.centroadoptivo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.ObservacionesException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;
import co.edu.unbosque.centroadoptivo.repository.AnimalRepository;
import co.edu.unbosque.centroadoptivo.repository.ResultadoIARepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.repository.ValidacionIARepository;
import co.edu.unbosque.centroadoptivo.util.ImageUtil;

/**
 * Servicio que gestiona todas las operaciones relacionadas con los animales
 * en el sistema de adopción, incluyendo registro con validación por IA,
 * consulta, actualización y eliminación.
 * <p>
 * Durante el registro de un animal, coordina con {@link IAOrquestadorService}
 * para detectar automáticamente la especie, raza, color, edad y clasificación
 * del animal a partir de su imagen, y valida que la información sea coherente
 * antes de persistirla en la base de datos.
 * </p>
 *
 * @author Centro Adoptivo
 * @version 1.0
 */
@Service
public class AnimalService {

    /** Repositorio para operaciones CRUD sobre animales. */
    @Autowired
    private AnimalRepository animalRepository;

    /** Repositorio para operaciones CRUD sobre usuarios. */
    @Autowired
    private UserRepository userRepository;

    /** Repositorio para persistir las validaciones de IA. */
    @Autowired
    private ValidacionIARepository validacionIARepository;

    /** Repositorio para persistir los resultados detallados de las IAs. */
    @Autowired
    private ResultadoIARepository resultadoIARepository;

    /** Servicio orquestador que coordina las llamadas a las 5 IAs. */
    @Autowired
    private IAOrquestadorService iaOrquestadorService;

    /**
     * Directorio donde se almacenan las imágenes de los animales.
     * Configurable desde {@code application.properties}.
     */
 

    /**
     * Registra un nuevo animal en el sistema, validando los datos básicos
     * ingresados por el usuario y utilizando 5 IAs para detectar automáticamente
     * la especie, raza, color, edad y clasificación del animal a partir de su imagen.
     * <p>
     * El animal solo se persiste si obtiene suficientes votos de aprobación
     * de las IAs participantes. Los resultados de la validación se guardan
     * en {@link ResultadoIA} y {@link ValidacionIA}.
     * </p>
     *
     * @param dto            DTO con los datos básicos del animal (nombre, observaciones,
     *                       esterilizado, vacunado)
     * @param imagen         imagen del animal a analizar por las IAs
     * @param usernameActual nombre de usuario del publicador autenticado
     * @return {@link AnimalDTO} con todos los datos del animal registrado,
     *         incluyendo los detectados por las IAs
     * @throws NombreException       si el nombre del animal no es válido
     * @throws ObservacionesException si las observaciones no cumplen los requisitos
     * @throws ImagenException        si la imagen no es válida (formato o tamaño)
     * @throws ValidacionIAException  si el animal no pasa la validación de las IAs
     * @throws UserNotFoundException  si el usuario publicador no existe en el sistema
     * @throws IOException            si ocurre un error al guardar la imagen en disco
     */
    public AnimalDTO registrarAnimal(
            AnimalDTO dto,
            MultipartFile imagen,
            String usernameActual)
            throws NombreException,
            ObservacionesException,
            ImagenException,
            ValidacionIAException,
            UserNotFoundException,
            IOException {

        LanzadorDeExcepcion.verificarNombreAnimal(dto.getName());
        LanzadorDeExcepcion.verificarObservaciones(dto.getObservations());
        LanzadorDeExcepcion.verificarImagen(imagen);

        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));

        User publisher = userRepository
                .findByUsername(usernameActual).get();

        byte[] imagenBytes = ImageUtil.obtenerBytes(imagen);
        String imagenBase64 = ImageUtil.convertirABase64(imagenBytes);

        ResultadoIADTO resultadoIA = iaOrquestadorService.detectarYValidar(
                imagenBase64,
                imagenBytes,
                dto.getObservations()
        );

        if (!resultadoIA.isAprobado()) {
            throw new ValidacionIAException(
                "La imagen no pasó la validación de las IAs. " +
                "Detalle: " + resultadoIA.getDetalle()
            );
        }

        String urlImagen = guardarImagen(imagen);

        Animal animal = new Animal();
        animal.setName(dto.getName());
        animal.setObservations(dto.getObservations());
        animal.setSterilized(dto.isSterilized());
        animal.setVaccinated(dto.isVaccinated());
        animal.setImage(urlImagen);
        animal.setPublishedAt(LocalDateTime.now());
        animal.setUpdatedAt(LocalDateTime.now());
        animal.setPublisher(publisher);
        animal.setAdopter(null);
        animal.setStatus(AnimalStatus.AVAILABLE);

        animal.setSpecies(resultadoIA.getEspecie());
        animal.setBreed(resultadoIA.getRaza());
        animal.setColor(resultadoIA.getColor());

        try {
            animal.setAge(AnimalAge.valueOf(resultadoIA.getEdad()));
        } catch (Exception e) {
            animal.setAge(AnimalAge.ADULT);
        }

        try {
            animal.setClassification(
                AnimalClassification.valueOf(resultadoIA.getClasificacion()));
        } catch (Exception e) {
            animal.setClassification(AnimalClassification.DOMESTIC);
        }

        Animal savedAnimal = animalRepository.save(animal);

        ResultadoIA resultadoGuardado = new ResultadoIA(
                resultadoIA.getEspecie(),
                resultadoIA.getRaza(),
                resultadoIA.getColor(),
                resultadoIA.getEdad(),
                resultadoIA.getClasificacion(),
                resultadoIA.isAprobado(),
                resultadoIA.getVotos(),
                resultadoIA.getTotalIAs(),
                resultadoIA.getDetalle(),
                LocalDateTime.now(),
                savedAnimal
        );
        resultadoIARepository.save(resultadoGuardado);

        ValidacionIA validacion = new ValidacionIA(
                resultadoIA.isAprobado(),
                resultadoIA.getVotos(),
                resultadoIA.getTotalIAs(),
                resultadoIA.getDetalle(),
                LocalDateTime.now(),
                savedAnimal
        );
        validacionIARepository.save(validacion);

        return convertirADTO(savedAnimal);
    }

    /**
     * Obtiene la lista de todos los animales con estado {@code AVAILABLE}
     * disponibles para adopción.
     *
     * @return lista de {@link AnimalDTO} con los animales disponibles,
     *         vacía si no hay ninguno
     */
    public List<AnimalDTO> obtenerAnimalesDisponibles() {
        return animalRepository
                .findByStatus(AnimalStatus.AVAILABLE)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene la lista completa de todos los animales registrados en el sistema,
     * independientemente de su estado.
     *
     * @return lista de {@link AnimalDTO} con todos los animales registrados,
     *         vacía si no hay ninguno
     */
    public List<AnimalDTO> obtenerTodos() {
        return animalRepository
                .findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene un animal específico por su identificador único.
     *
     * @param id identificador único del animal a buscar
     * @return {@link AnimalDTO} con los datos del animal encontrado
     * @throws AnimalNoEncontradoException si no existe un animal con el ID indicado
     */
    public AnimalDTO obtenerAnimalPorId(Long id)
            throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(
                animalRepository.existsById(id));
        return convertirADTO(animalRepository.findById(id).get());
    }

    /**
     * Obtiene todos los animales publicados por un usuario específico.
     *
     * @param publisherId identificador único del usuario publicador
     * @return lista de {@link AnimalDTO} con los animales del publicador,
     *         vacía si no ha publicado ninguno
     */
    public List<AnimalDTO> obtenerAnimalesPorPublicador(Long publisherId) {
        return animalRepository
                .findByPublisherId(publisherId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Actualiza los datos básicos de un animal existente (nombre, observaciones,
     * esterilizado y vacunado). No modifica los datos detectados por las IAs.
     *
     * @param id  identificador único del animal a actualizar
     * @param dto DTO con los nuevos datos del animal
     * @return {@link AnimalDTO} con los datos actualizados del animal
     * @throws AnimalNoEncontradoException si no existe un animal con el ID indicado
     * @throws NombreException             si el nuevo nombre del animal no es válido
     * @throws ObservacionesException      si las nuevas observaciones no son válidas
     */
    public AnimalDTO actualizarAnimal(Long id, AnimalDTO dto)
            throws AnimalNoEncontradoException,
            NombreException,
            ObservacionesException {

        LanzadorDeExcepcion.verificarAnimalExiste(
                animalRepository.existsById(id));
        LanzadorDeExcepcion.verificarNombreAnimal(dto.getName());
        LanzadorDeExcepcion.verificarObservaciones(dto.getObservations());

        Animal animal = animalRepository.findById(id).get();
        animal.setName(dto.getName());
        animal.setObservations(dto.getObservations());
        animal.setSterilized(dto.isSterilized());
        animal.setVaccinated(dto.isVaccinated());
        animal.setUpdatedAt(LocalDateTime.now());

        return convertirADTO(animalRepository.save(animal));
    }

    /**
     * Elimina un animal del sistema por su identificador único.
     *
     * @param id identificador único del animal a eliminar
     * @throws AnimalNoEncontradoException si no existe un animal con el ID indicado
     */
    public void eliminarAnimal(Long id)
            throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(
                animalRepository.existsById(id));
        animalRepository.deleteById(id);
    }

    /**
     * Obtiene el identificador único de un usuario a partir de su nombre de usuario.
     *
     * @param username nombre de usuario a buscar
     * @return identificador único del usuario encontrado
     * @throws UserNotFoundException si no existe un usuario con ese nombre de usuario
     */
    public Long obtenerIdPorUsername(String username)
            throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(username));
        return userRepository.findByUsername(username).get().getId();
    }

    /**
     * Busca un animal por su identificador único sin lanzar excepción si no existe.
     *
     * @param id identificador único del animal a buscar
     * @return {@link Optional} con el animal si existe, vacío si no
     */
    public Optional<Animal> findById(Long id) {
        return animalRepository.findById(id);
    }

    /**
     * Convierte la imagen del animal a Base64 para almacenarla
     * directamente en la base de datos, evitando dependencia del sistema de archivos.
     *
     * @param imagen archivo de imagen a convertir
     * @return cadena Base64 con el prefijo data URI listo para usar en el frontend
     * @throws IOException si ocurre un error durante la lectura del archivo
     */
    private String guardarImagen(MultipartFile imagen) throws IOException {
        byte[] bytes = imagen.getBytes();
        String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
        String contentType = imagen.getContentType();
        return "data:" + contentType + ";base64," + base64;
    }

    /**
     * Convierte una entidad {@link Animal} a su representación como
     * {@link AnimalDTO} para ser retornada al cliente.
     *
     * @param animal entidad animal a convertir
     * @return {@link AnimalDTO} con todos los datos del animal
     */
    private AnimalDTO convertirADTO(Animal animal) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setAge(animal.getAge());
        dto.setSterilized(animal.isSterilized());
        dto.setVaccinated(animal.isVaccinated());
        dto.setSpecies(animal.getSpecies());
        dto.setBreed(animal.getBreed());
        dto.setColor(animal.getColor());
        dto.setObservations(animal.getObservations());
        dto.setImage(animal.getImage());
        dto.setPublishedAt(animal.getPublishedAt());
        dto.setUpdatedAt(animal.getUpdatedAt());
        dto.setClassification(animal.getClassification());
        dto.setStatus(animal.getStatus());
        dto.setPublisherId(animal.getPublisher() != null
                ? animal.getPublisher().getId() : null);
        dto.setAdopterId(animal.getAdopter() != null
                ? animal.getAdopter().getId() : null);
        return dto;
    }
}