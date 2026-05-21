package co.edu.unbosque.centroadoptivo.controller;

import co.edu.unbosque.centroadoptivo.dto.SolicitudAdopcionDTO;
import co.edu.unbosque.centroadoptivo.service.SolicitudAdopcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudAdopcionController {

    @Autowired
    private SolicitudAdopcionService solicitudService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearSolicitud(
            @RequestParam Long animalId,
            @RequestParam Long adopterId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(solicitudService.crearSolicitud(animalId, adopterId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(solicitudService.aprobarSolicitud(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/rechazar/{id}")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable Long id,
            @RequestParam String rejectionReason) {
        try {
            return ResponseEntity.ok(solicitudService.rechazarSolicitud(id, rejectionReason));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudAdopcionDTO>> getPendientes() {
        return ResponseEntity.ok(solicitudService.obtenerPendientes());
    }

    @GetMapping("/adoptante/{adopterId}")
    public ResponseEntity<List<SolicitudAdopcionDTO>> getByAdopter(@PathVariable Long adopterId) {
        return ResponseEntity.ok(solicitudService.obtenerPorAdoptante(adopterId));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<SolicitudAdopcionDTO>> getByAnimal(@PathVariable Long animalId) {
        return ResponseEntity.ok(solicitudService.obtenerPorAnimal(animalId));
    }
}