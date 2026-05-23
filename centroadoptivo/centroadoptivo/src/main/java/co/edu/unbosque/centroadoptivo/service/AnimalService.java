package co.edu.unbosque.centroadoptivo.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidacionIARepository validacionIARepository;

    @Autowired
    private ResultadoIARepository resultadoIARepository;

    @Autowired
    private IAOrquestadorService iaOrquestadorService;

    @Value("${app.imagenes.directorio:uploads/animales}")
    private String directorioImagenes;

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

        // Validamos solo nombre, observaciones e imagen
        // especie, raza, color, edad y clasificacion los detecta la IA
        LanzadorDeExcepcion.verificarNombreAnimal(dto.getName());
        LanzadorDeExcepcion.verificarObservaciones(dto.getObservations());
        LanzadorDeExcepcion.verificarImagen(imagen);

        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));

        User publisher = userRepository
                .findByUsername(usernameActual).get();

        byte[] imagenBytes = ImageUtil.obtenerBytes(imagen);
        String imagenBase64 = ImageUtil.convertirABase64(imagenBytes);

        // Las IAs detectan y validan automáticamente
        ResultadoIADTO resultadoIA = iaOrquestadorService.detectarYValidar(
                imagenBase64,
                imagenBytes,
                dto.getObservations()
        );

        // Si no pasó la validación no se guarda
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

        // Datos detectados por las IAs
        animal.setSpecies(resultadoIA.getEspecie());
        animal.setBreed(resultadoIA.getRaza());
        animal.setColor(resultadoIA.getColor());

        // Convertir edad a enum — si falla usamos ADULT por defecto
        try {
            animal.setAge(AnimalAge.valueOf(resultadoIA.getEdad()));
        } catch (Exception e) {
            animal.setAge(AnimalAge.ADULT);
        }

        // Convertir clasificacion a enum — si falla usamos DOMESTIC por defecto
        try {
            animal.setClassification(
                AnimalClassification.valueOf(resultadoIA.getClasificacion()));
        } catch (Exception e) {
            animal.setClassification(AnimalClassification.DOMESTIC);
        }

        Animal savedAnimal = animalRepository.save(animal);

        // Guardar resultado de las IAs
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

    public List<AnimalDTO> obtenerAnimalesDisponibles() {
        return animalRepository
                .findByStatus(AnimalStatus.AVAILABLE)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<AnimalDTO> obtenerTodos() {
        return animalRepository
                .findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public AnimalDTO obtenerAnimalPorId(Long id)
            throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(
                animalRepository.existsById(id));
        return convertirADTO(animalRepository.findById(id).get());
    }

    public List<AnimalDTO> obtenerAnimalesPorPublicador(Long publisherId) {
        return animalRepository
                .findByPublisherId(publisherId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

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

    public void eliminarAnimal(Long id)
            throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(
                animalRepository.existsById(id));
        animalRepository.deleteById(id);
    }

    public Long obtenerIdPorUsername(String username)
            throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(username));
        return userRepository.findByUsername(username).get().getId();
    }

    public Optional<Animal> findById(Long id) {
        return animalRepository.findById(id);
    }

    private String guardarImagen(MultipartFile imagen) throws IOException {
        Path directorio = Paths.get(directorioImagenes);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
        String nombreArchivo = UUID.randomUUID()
                + "_" + imagen.getOriginalFilename();
        Path destino = directorio.resolve(nombreArchivo);
        Files.copy(imagen.getInputStream(), destino);
        return "/" + directorioImagenes + "/" + nombreArchivo;
    }

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