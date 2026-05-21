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
@RequestMapping("/api/animals")
@CrossOrigin(origins = "*")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAnimal(
            @RequestPart("data") AnimalDTO dto,
            @RequestPart("image") MultipartFile image) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(animalService.registrarAnimal(dto, image));
        } catch (NombreException | EspecieException | RazaException |
                 ObservacionesException | ImagenException | ValidacionIAException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving image");
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<AnimalDTO>> getAvailable() {
        return ResponseEntity.ok(animalService.obtenerAnimalesDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(animalService.obtenerAnimalPorId(id));
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<AnimalDTO>> getByPublisher(@PathVariable Long publisherId) {
        return ResponseEntity.ok(animalService.obtenerAnimalesPorPublicador(publisherId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnimal(
            @PathVariable Long id,
            @RequestBody AnimalDTO dto) {
        try {
            return ResponseEntity.ok(animalService.actualizarAnimal(id, dto));
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NombreException | ObservacionesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.eliminarAnimal(id);
            return ResponseEntity.noContent().build();
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}