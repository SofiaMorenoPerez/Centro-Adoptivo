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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de notificaciones del usuario autenticado.
 * <p>
 * Expone endpoints para consultar, contar y marcar como leídas las notificaciones.
 * Requiere autenticación mediante JWT.
 * </p>
 */
@RestController
@RequestMapping("/notificacion")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8081", "*" })
@Transactional
@Tag(name = "Notificaciones", description = "Endpoints para el sistema de notificaciones")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    /** Servicio de lógica de negocio para notificaciones. */
    @Autowired
    private NotificacionService notificacionService;

    /** Servicio de auditoría para registrar acciones del sistema. */
    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Obtiene el nombre del usuario autenticado en el contexto de seguridad actual.
     *
     * @return nombre de usuario autenticado
     */
    private String getUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Obtiene todas las notificaciones del usuario autenticado.
     *
     * @return lista de {@link NotificacionDTO}, 204 si no hay notificaciones,
     *         o 404 si el usuario no es encontrado
     */
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

    /**
     * Obtiene las notificaciones no leídas del usuario autenticado.
     *
     * @return lista de {@link NotificacionDTO} no leídas, 204 si no hay ninguna,
     *         o 404 si el usuario no es encontrado
     */
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

    /**
     * Cuenta el número de notificaciones no leídas del usuario autenticado.
     *
     * @return cantidad de notificaciones no leídas, o 404 si el usuario no es encontrado
     */
    @Operation(summary = "Contar notificaciones no leídas")
    @GetMapping("/contar")
    public ResponseEntity<?> contar() {
        try {
            long count = notificacionService.contarNoLeidas(getUsuarioActual());
            return ResponseEntity.ok(count);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    /**
     * Marca una notificación específica como leída.
     *
     * @param id identificador de la notificación a marcar
     * @return 200 si fue marcada correctamente, o 404 si la notificación no existe
     */
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

    /**
     * Marca todas las notificaciones del usuario autenticado como leídas.
     *
     * @return 200 si todas fueron marcadas correctamente, o 404 si el usuario no es encontrado
     */
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