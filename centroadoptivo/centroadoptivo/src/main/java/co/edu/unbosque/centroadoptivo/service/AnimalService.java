package co.edu.unbosque.centroadoptivo.service;

import co.edu.unbosque.centroadoptivo.dto.AnimalDTO;
import co.edu.unbosque.centroadoptivo.dto.ValidacionIADTO;
import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalEstado;
import co.edu.unbosque.centroadoptivo.entity.Usuario;
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
import co.edu.unbosque.centroadoptivo.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ValidacionIARepository validacionIARepository;

    @Autowired
    private IAOrquestadorService iaOrquestadorService;

    @Value("${app.imagenes.directorio:uploads/animales}")
    private String directorioImagenes;

    public AnimalDTO registrarAnimal(AnimalDTO dto, MultipartFile imagen)
            throws NombreException, EspecieException, RazaException,
            ObservacionesException, ImagenException, ValidacionIAException, IOException {

        LanzadorDeExcepcion.verificarNombreAnimal(dto.getNombre());
        LanzadorDeExcepcion.verificarEspecie(dto.getEspecie());
        LanzadorDeExcepcion.verificarRaza(dto.getRaza());
        LanzadorDeExcepcion.verificarObservaciones(dto.getObservaciones());
        LanzadorDeExcepcion.verificarImagen(imagen);

        String imagenBase64 = Base64.getEncoder().encodeToString(imagen.getBytes());

        ValidacionIADTO resultadoValidacion = iaOrquestadorService.validarMascota(
                imagenBase64,
                dto.getEspecie(),
                dto.getRaza(),
                dto.getObservaciones()
        );

        String urlImagen = guardarImagen(imagen);

        Usuario publicador = usuarioRepository.findById(dto.getPublicadorId()).get();

        Animal animal = new Animal();
        animal.setNombre(dto.getNombre());
        animal.setEspecie(dto.getEspecie());
        animal.setRaza(dto.getRaza());
        animal.setColor(dto.getColor());
        animal.setEdad(dto.getEdad());
        animal.setEsterilizado(dto.isEsterilizado());
        animal.setVacunado(dto.isVacunado());
        animal.setObservaciones(dto.getObservaciones());
        animal.setImagen(urlImagen);
        animal.setClasificacion(dto.getClasificacion());
        animal.setPublicadoEn(LocalDateTime.now());
        animal.setActualizadoEn(LocalDateTime.now());
        animal.setPublicador(publicador);
        animal.setAdoptante(null);

        if (resultadoValidacion.isAprobado()) {
            animal.setEstado(AnimalEstado.DISPONIBLE);
        } else {
            animal.setEstado(AnimalEstado.PENDIENTE);
        }

        Animal animalGuardado = animalRepository.save(animal);

        ValidacionIA validacion = new ValidacionIA(
                resultadoValidacion.isAprobado(),
                resultadoValidacion.getVotos(),
                resultadoValidacion.getTotalIAs(),
                resultadoValidacion.getDetalle(),
                LocalDateTime.now(),
                animalGuardado
        );
        validacionIARepository.save(validacion);

        return convertirADTO(animalGuardado);
    }

    public List<AnimalDTO> obtenerAnimalesDisponibles() {
        return animalRepository.findByEstado(AnimalEstado.DISPONIBLE)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public AnimalDTO obtenerAnimalPorId(long id) throws AnimalNoEncontradoException {
        LanzadorDeExcepcion.verificarAnimalExiste(animalRepository.existsById(id));
        Animal animal = animalRepository.findById(id).get();
        return convertirADTO(animal);
    }

    public List<AnimalDTO> obtenerAnimalesPorPublicador(long publicadorId) {
        return animalRepository.findByPublicadorId(publicadorId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public AnimalDTO actualizarAnimal(long id, AnimalDTO dto)
            throws AnimalNoEncontradoException, NombreException, ObservacionesException {

        LanzadorDeExcepcion.verificarAnimalExiste(animalRepository.existsById(id));
        LanzadorDeExcepcion.verificarNombreAnimal(dto.getNombre());
        LanzadorDeExcepcion.verificarObservaciones(dto.getObservaciones());

        Animal animal = animalRepository.findById(id).get();
        animal.setNombre(dto.getNombre());
        animal.setObservaciones(dto.getObservaciones());
        animal.setEsterilizado(dto.isEsterilizado());
        animal.setVacunado(dto.isVacunado());
        animal.setActualizadoEn(LocalDateTime.now());

        return convertirADTO(animalRepository.save(animal));
    }

    public void eliminarAnimal(long id) throws AnimalNoEncontradoException {
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
        dto.setNombre(animal.getNombre());
        dto.setEdad(animal.getEdad());
        dto.setEsterilizado(animal.isEsterilizado());
        dto.setVacunado(animal.isVacunado());
        dto.setEspecie(animal.getEspecie());
        dto.setRaza(animal.getRaza());
        dto.setColor(animal.getColor());
        dto.setObservaciones(animal.getObservaciones());
        dto.setImagen(animal.getImagen());
        dto.setPublicadoEn(animal.getPublicadoEn());
        dto.setActualizadoEn(animal.getActualizadoEn());
        dto.setClasificacion(animal.getClasificacion());
        dto.setEstado(animal.getEstado());
        dto.setPublicadorId(animal.getPublicador() != null ? animal.getPublicador().getId() : 0);
        dto.setAdoptanteId(animal.getAdoptante() != null ? animal.getAdoptante().getId() : 0);
        return dto;
    }
}