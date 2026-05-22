package co.edu.unbosque.centroadoptivo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.centroadoptivo.dto.NotificacionDTO;
import co.edu.unbosque.centroadoptivo.exception.NotificacionNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.service.AuditoriaService;
import co.edu.unbosque.centroadoptivo.service.NotificacionService;
import co.edu.unbosque.centroadoptivo.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:4200" })
@Transactional
@Tag(name = "Notificaciones", description = "Endpoints para el sistema de notificaciones")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private AuditoriaService auditoriaService;

    private String getUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    @Operation(summary = "Obtener todas mis notificaciones")
    @GetMapping("/mis")
    public ResponseEntity<?> getMis() {
        try {
            List<NotificacionDTO> list = notificacionService
                    .obtenerMisNotificaciones(getUsuarioActual());
            if (list.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(list);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    @Operation(summary = "Obtener notificaciones no leídas")
    @GetMapping("/noleidas")
    public ResponseEntity<?> getNoLeidas() {
        try {
            List<NotificacionDTO> list = notificacionService
                    .obtenerNoLeidas(getUsuarioActual());
            if (list.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(list);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    @Operation(summary = "Contar notificaciones no leídas (campana)")
    @GetMapping("/contar")
    public ResponseEntity<?> contar() {
        try {
            return ResponseEntity.ok(notificacionService.contarNoLeidas(getUsuarioActual()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    @Operation(summary = "Marcar una notificación como leída")
    @PatchMapping("/leer/{id}")
    public ResponseEntity<?> marcarLeida(@PathVariable Long id) {
        try {
            notificacionService.marcarComoLeida(id);
            auditoriaService.registrar(getUsuarioActual(), "LEER_NOTIFICACION",
                    "Marcó notificación id=" + id + " como leída", true);
            return ResponseEntity.ok().build();
        } catch (NotificacionNoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificación no encontrada");
        }
    }

    @Operation(summary = "Marcar todas las notificaciones como leídas")
    @PatchMapping("/leer/todas")
    public ResponseEntity<?> marcarTodasLeidas() {
        try {
            notificacionService.marcarTodasComoLeidas(getUsuarioActual());
            auditoriaService.registrar(getUsuarioActual(), "LEER_TODAS_NOTIFICACIONES",
                    "Marcó todas las notificaciones como leídas", true);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }
}