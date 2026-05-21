package co.edu.unbosque.centroadoptivo.controller;

import co.edu.unbosque.centroadoptivo.dto.AnimalDTO;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.ImagenException;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.ObservacionesException;
import co.edu.unbosque.centroadoptivo.exception.RazaException;
import co.edu.unbosque.centroadoptivo.exception.EspecieException;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;
import co.edu.unbosque.centroadoptivo.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/animales")
@CrossOrigin(origins = "*")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAnimal(
            @RequestPart("datos") AnimalDTO dto,
            @RequestPart("imagen") MultipartFile imagen) {
        try {
            AnimalDTO resultado = animalService.registrarAnimal(dto, imagen);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (NombreException | EspecieException | RazaException |
                 ObservacionesException | ImagenException | ValidacionIAException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la imagen");
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<AnimalDTO>> obtenerDisponibles() {
        return ResponseEntity.ok(animalService.obtenerAnimalesDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable long id) {
        try {
            return ResponseEntity.ok(animalService.obtenerAnimalPorId(id));
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/publicador/{publicadorId}")
    public ResponseEntity<List<AnimalDTO>> obtenerPorPublicador(
            @PathVariable long publicadorId) {
        return ResponseEntity.ok(animalService.obtenerAnimalesPorPublicador(publicadorId));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarAnimal(
            @PathVariable long id,
            @RequestBody AnimalDTO dto) {
        try {
            return ResponseEntity.ok(animalService.actualizarAnimal(id, dto));
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NombreException | ObservacionesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarAnimal(@PathVariable long id) {
        try {
            animalService.eliminarAnimal(id);
            return ResponseEntity.noContent().build();
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}