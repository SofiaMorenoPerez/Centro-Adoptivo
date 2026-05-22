package co.edu.unbosque.centroadoptivo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import co.edu.unbosque.centroadoptivo.service.AnimalService;
import co.edu.unbosque.centroadoptivo.service.AuditoriaService;
import co.edu.unbosque.centroadoptivo.util.AESUtil;
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
    private AuditoriaService auditoriaService;

    private String getUsuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return AESUtil.decrypt(username);
        } catch (Exception e) {
            return username;
        }
    }

    @Operation(summary = "Registrar animal con validación IA")
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestPart("data") AnimalDTO dto,
            @RequestPart("image") MultipartFile image) {
        try {
            AnimalDTO resultado = animalService.registrarAnimal(dto, image, getUsuarioActual());
            auditoriaService.registrar(getUsuarioActual(), "REGISTER_ANIMAL",
                "Registró animal con nombre=" + dto.getName(), true);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (ValidacionIAException e) {
            auditoriaService.registrar(getUsuarioActual(), "REGISTER_ANIMAL",
                "Registro fallido por validación IA — animal=" + dto.getName()
                + " | " + e.getMessage(), false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NombreException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("El nombre del animal no es válido");
        } catch (EspecieException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("La especie no es válida");
        } catch (RazaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("La raza no es válida");
        } catch (ObservacionesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Las observaciones no son válidas (mínimo 10, máximo 500 caracteres)");
        } catch (ImagenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("La imagen no es válida (debe ser JPG o PNG, máximo 5MB)");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Usuario no encontrado");
        } catch (IOException e) {
            auditoriaService.registrar(getUsuarioActual(), "REGISTER_ANIMAL",
                "Error al guardar imagen del animal=" + dto.getName(), false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al guardar la imagen");
        }
    }

    @Operation(summary = "Obtener todos los animales disponibles")
    @GetMapping("/getall")
    public ResponseEntity<List<AnimalDTO>> getAll() {
        List<AnimalDTO> list = animalService.obtenerAnimalesDisponibles();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Obtener todos los animales (ADMIN)")
    @GetMapping("/getallAdmin")
    public ResponseEntity<List<AnimalDTO>> getAllAdmin() {
        List<AnimalDTO> list = animalService.obtenerTodos();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Obtener animal por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(animalService.obtenerAnimalPorId(id));
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Animal no encontrado");
        }
    }

    @Operation(summary = "Obtener animales por publicador")
    @GetMapping("/publicador/{publisherId}")
    public ResponseEntity<List<AnimalDTO>> getByPublisher(@PathVariable Long publisherId) {
        return ResponseEntity.ok(animalService.obtenerAnimalesPorPublicador(publisherId));
    }

    @Operation(summary = "Mis publicaciones (usuario autenticado)")
    @GetMapping("/mispublicaciones")
    public ResponseEntity<?> misPublicaciones() {
        try {
            Long publisherId = animalService.obtenerIdPorUsername(getUsuarioActual());
            List<AnimalDTO> list = animalService.obtenerAnimalesPorPublicador(publisherId);
            if (list.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(list);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Usuario no encontrado");
        }
    }

    @Operation(summary = "Actualizar animal")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AnimalDTO dto) {
        try {
            AnimalDTO resultado = animalService.actualizarAnimal(id, dto);
            auditoriaService.registrar(getUsuarioActual(), "UPDATE_ANIMAL",
                "Actualizó animal con id=" + id, true);
            return ResponseEntity.ok(resultado);
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Animal no encontrado");
        } catch (NombreException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("El nombre del animal no es válido");
        } catch (ObservacionesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Las observaciones no son válidas");
        }
    }

    @Operation(summary = "Eliminar animal")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            animalService.eliminarAnimal(id);
            auditoriaService.registrar(getUsuarioActual(), "DELETE_ANIMAL",
                "Eliminó animal con id=" + id, true);
            return ResponseEntity.noContent().build();
        } catch (AnimalNoEncontradoException e) {
            auditoriaService.registrar(getUsuarioActual(), "DELETE_ANIMAL",
                "Intento fallido de eliminar animal con id=" + id + " — no encontrado", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Animal no encontrado");
        }
    }
}