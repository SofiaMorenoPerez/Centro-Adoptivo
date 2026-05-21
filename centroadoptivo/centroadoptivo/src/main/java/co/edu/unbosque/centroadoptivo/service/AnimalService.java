package co.edu.unbosque.centroadoptivo.service;

import co.edu.unbosque.centroadoptivo.dto.AnimalDTO;
import co.edu.unbosque.centroadoptivo.dto.ValidacionIADTO;
import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.entity.ValidacionIA;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.ImagenException;
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.ObservacionesException;
import co.edu.unbosque.centroadoptivo.exception.RazaException;
import co.edu.unbosque.centroadoptivo.exception.EspecieException;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;
import co.edu.unbosque.centroadoptivo.repository.AnimalRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.repository.ValidacionIARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private ValidacionIARepository validacionIARepository;

    @Autowired
    private IAOrquestadorService iaOrquestadorService;

    @Value("${app.imagenes.directorio:uploads/animales}")
    private String directorioImagenes;

    public AnimalDTO registrarAnimal(AnimalDTO dto, MultipartFile imagen)
            throws NombreException, EspecieException, RazaException,
            ObservacionesException, ImagenException, ValidacionIAException, IOException {

        LanzadorDeExcepcion.verificarNombreAnimal(dto.getName());
        LanzadorDeExcepcion.verificarEspecie(dto.getSpecies());
        LanzadorDeExcepcion.verificarRaza(dto.getBreed());
        LanzadorDeExcepcion.verificarObservaciones(dto.getObservations());
        LanzadorDeExcepcion.verificarImagen(imagen);

        String imagenBase64 = Base64.getEncoder().encodeToString(imagen.getBytes());

        // Llamada al orquestador con todos los campos necesarios
        ValidacionIADTO resultadoValidacion = iaOrquestadorService.validarMascota(
                imagenBase64,
                dto.getSpecies(),
                dto.getBreed(),
                dto.getColor(),
                dto.getAge().name(),
                dto.getClassification().name(),
                dto.getObservations()
        );

        // Si las IAs rechazan, no se guarda el animal
        if (!resultadoValidacion.isAprobado()) {
            throw new ValidacionIAException(
                "La información del animal no coincide con la imagen. " +
                "Detalle: " + resultadoValidacion.getDetalle()
            );
        }

        String urlImagen = guardarImagen(imagen);
        User publisher = usuarioRepository.findById(dto.getPublisherId()).get();

        Animal animal = new Animal();
        animal.setName(dto.getName());
        animal.setSpecies(dto.getSpecies());
        animal.setBreed(dto.getBreed());
        animal.setColor(dto.getColor());
        animal.setAge(dto.getAge());
        animal.setSterilized(dto.isSterilized());
        animal.setVaccinated(dto.isVaccinated());
        animal.setObservations(dto.getObservations());
        animal.setImage(urlImagen);
        animal.setClassification(dto.getClassification());
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setPublishedAt(LocalDateTime.now());
        animal.setUpdatedAt(LocalDateTime.now());
        animal.setPublisher(publisher);
        animal.setAdopter(null);

        Animal savedAnimal = animalRepository.save(animal);

        ValidacionIA validacion = new ValidacionIA(
                resultadoValidacion.isAprobado(),
                resultadoValidacion.getVotos(),
                resultadoValidacion.getTotalIAs(),
                resultadoValidacion.getDetalle(),
                LocalDateTime.now(),
                savedAnimal
        );
        validacionIARepository.save(validacion);

        return convertirADTO(savedAnimal);
    }

    public List<AnimalDTO> obtenerAnimalesDisponibles() {
        return animalRepository.findByStatus(AnimalStatus.AVAILABLE)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public AnimalDTO obtenerAnimalPorId(Long id) throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(animalRepository.existsById(id));
        return convertirADTO(animalRepository.findById(id).get());
    }

    public List<AnimalDTO> obtenerAnimalesPorPublicador(Long publisherId) {
        return animalRepository.findByPublisherId(publisherId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public AnimalDTO actualizarAnimal(Long id, AnimalDTO dto)
            throws AnimalNoEncontradoException, NombreException, ObservacionesException {

        LanzadorDeExcepcion.verificarAnimalExiste(animalRepository.existsById(id));
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

    public void eliminarAnimal(Long id) throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(animalRepository.existsById(id));
        animalRepository.deleteById(id);
    }

    private String guardarImagen(MultipartFile imagen) throws IOException {
        Path directorio = Paths.get(directorioImagenes);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
        String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
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
        dto.setPublisherId(animal.getPublisher() != null ? animal.getPublisher().getId() : null);
        dto.setAdopterId(animal.getAdopter() != null ? animal.getAdopter().getId() : null);
        return dto;
    }
}