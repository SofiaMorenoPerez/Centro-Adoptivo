package co.edu.unbosque.centroadoptivo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.centroadoptivo.dto.AnimalDTO;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.EspecieException;
import co.edu.unbosque.centroadoptivo.exception.ImagenException;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.ObservacionesException;
import co.edu.unbosque.centroadoptivo.exception.RazaException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/animal")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Animales", description = "Endpoints para administrar animales en adopción")
@SecurityRequirement(name = "bearerAuth")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private UserRepository userRepository;

    public AnimalController() {}

   
    @Operation(summary = "Registrar animal con imagen")
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAnimal(
            @RequestPart("datos") AnimalDTO dto,
            @RequestPart("imagen") MultipartFile imagen,
            Authentication authentication) {
        try {
           
            String username = authentication.getName();
            userRepository.findByUsername(username).ifPresent(user -> dto.setPublicadorId(user.getId()));

            AnimalDTO resultado = animalService.registrarAnimal(dto, imagen);
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
        } catch (NombreException e) {
            return new ResponseEntity<>("El nombre del animal no es válido", HttpStatus.BAD_REQUEST);
        } catch (EspecieException e) {
            return new ResponseEntity<>("La especie ingresada no es válida", HttpStatus.BAD_REQUEST);
        } catch (RazaException e) {
            return new ResponseEntity<>("La raza ingresada no es válida", HttpStatus.BAD_REQUEST);
        } catch (ObservacionesException e) {
            return new ResponseEntity<>("Las observaciones no son válidas", HttpStatus.BAD_REQUEST);
        } catch (ImagenException e) {
            return new ResponseEntity<>("La imagen no es válida (solo JPG/PNG, máx 5MB)", HttpStatus.BAD_REQUEST);
        } catch (ValidacionIAException e) {
            return new ResponseEntity<>("La validación de IA no fue aprobada", HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("Usuario publicador no encontrado", HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error al guardar la imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener animales disponibles")
    @GetMapping("/getall")
    public ResponseEntity<List<AnimalDTO>> obtenerDisponibles() {
        List<AnimalDTO> list = animalService.obtenerAnimalesDisponibles();
        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

  
    @Operation(summary = "Obtener animal por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable long id) {
        try {
            return new ResponseEntity<>(animalService.obtenerAnimalPorId(id), HttpStatus.ACCEPTED);
        } catch (AnimalNoEncontradoException e) {
            return new ResponseEntity<>("Animal no encontrado", HttpStatus.NOT_FOUND);
        }
    }

 
    @Operation(summary = "Obtener mis animales publicados")
    @GetMapping("/mispublicaciones")
    public ResponseEntity<List<AnimalDTO>> obtenerMisPublicaciones(Authentication authentication) {
        
        String username = authentication.getName();
        Long publicadorId = userRepository.findByUsername(username)
                .map(u -> u.getId()).orElse(null);
        if (publicadorId == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<AnimalDTO> list = animalService.obtenerAnimalesPorPublicador(publicadorId);
        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

   
    @Operation(summary = "Obtener animales por publicador")
    @GetMapping("/publicador/{publicadorId}")
    public ResponseEntity<List<AnimalDTO>> obtenerPorPublicador(@PathVariable long publicadorId) {
        List<AnimalDTO> list = animalService.obtenerAnimalesPorPublicador(publicadorId);
        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

   
    @Operation(summary = "Actualizar animal")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarAnimal(@PathVariable long id, @RequestBody AnimalDTO dto) {
        try {
            return new ResponseEntity<>(animalService.actualizarAnimal(id, dto), HttpStatus.ACCEPTED);
        } catch (AnimalNoEncontradoException e) {
            return new ResponseEntity<>("Animal no encontrado", HttpStatus.NOT_FOUND);
        } catch (NombreException e) {
            return new ResponseEntity<>("El nombre del animal no es válido", HttpStatus.BAD_REQUEST);
        } catch (ObservacionesException e) {
            return new ResponseEntity<>("Las observaciones no son válidas", HttpStatus.BAD_REQUEST);
        }
    }

   
    @Operation(summary = "Eliminar animal")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarAnimal(@PathVariable long id) {
        try {
            animalService.eliminarAnimal(id);
            return new ResponseEntity<>("Animal eliminado exitosamente", HttpStatus.ACCEPTED);
        } catch (AnimalNoEncontradoException e) {
            return new ResponseEntity<>("Animal no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}