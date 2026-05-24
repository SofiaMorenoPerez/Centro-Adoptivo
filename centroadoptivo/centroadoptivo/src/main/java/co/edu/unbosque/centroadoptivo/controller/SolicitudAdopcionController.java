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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.centroadoptivo.dto.SolicitudAdopcionDTO;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoDisponibleException;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudDuplicadaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoPendienteException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.service.AuditoriaService;
import co.edu.unbosque.centroadoptivo.service.SolicitudAdopcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de solicitudes de adopción.
 * <p>
 * Expone endpoints para crear, aprobar, rechazar y consultar solicitudes de adopción.
 * Requiere autenticación mediante JWT.
 * </p>
 */
@RestController
@RequestMapping("/solicitud")
@Transactional
@Tag(name = "Gestión de Solicitudes", description = "Endpoints para administrar solicitudes de adopción")
@SecurityRequirement(name = "bearerAuth")
public class SolicitudAdopcionController {

    /** Servicio de lógica de negocio para solicitudes de adopción. */
    @Autowired
    private SolicitudAdopcionService solicitudService;

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
     * Crea una nueva solicitud de adopción para el animal indicado.
     *
     * @param animalId identificador del animal a adoptar
     * @return el {@link SolicitudAdopcionDTO} creado, o un mensaje de error según la excepción
     */
    @Operation(summary = "Crear solicitud de adopción")
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestParam Long animalId) {
        try {
            SolicitudAdopcionDTO resultado = solicitudService.crearSolicitud(
                    animalId, getUsuarioActual());
            auditoriaService.registrar(getUsuarioActual(), "CREATE_SOLICITUD",
                    "Creó solicitud para animal id=" + animalId, true);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (AnimalNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Animal no encontrado");
        } catch (AnimalNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El animal no está disponible para adopción");
        } catch (SolicitudDuplicadaException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya tienes una solicitud pendiente para este animal");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    /**
     * Aprueba una solicitud de adopción pendiente (uso exclusivo de ADMIN).
     *
     * @param id identificador de la solicitud a aprobar
     * @return el {@link SolicitudAdopcionDTO} aprobado, o un mensaje de error según la excepción
     */
    @Operation(summary = "Aprobar solicitud (ADMIN)")
    @PatchMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobar(@PathVariable Long id) {
        try {
            SolicitudAdopcionDTO resultado = solicitudService.aprobarSolicitud(id);
            auditoriaService.registrar(getUsuarioActual(), "APPROVE_SOLICITUD",
                    "Aprobó solicitud id=" + id, true);
            return ResponseEntity.ok(resultado);
        } catch (SolicitudNoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Solicitud no encontrada");
        } catch (SolicitudNoPendienteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La solicitud no está en estado pendiente");
        }
    }

    /**
     * Rechaza una solicitud de adopción pendiente con una razón (uso exclusivo de ADMIN).
     *
     * @param id              identificador de la solicitud a rechazar
     * @param rejectionReason motivo del rechazo
     * @return el {@link SolicitudAdopcionDTO} rechazado, o un mensaje de error según la excepción
     */
    @Operation(summary = "Rechazar solicitud (ADMIN)")
    @PatchMapping("/rechazar/{id}")
    public ResponseEntity<?> rechazar(
            @PathVariable Long id,
            @RequestParam String rejectionReason) {
        try {
            SolicitudAdopcionDTO resultado = solicitudService.rechazarSolicitud(
                    id, rejectionReason);
            auditoriaService.registrar(getUsuarioActual(), "REJECT_SOLICITUD",
                    "Rechazó solicitud id=" + id + " — razón: " + rejectionReason, true);
            return ResponseEntity.ok(resultado);
        } catch (SolicitudNoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Solicitud no encontrada");
        } catch (SolicitudNoPendienteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La solicitud no está en estado pendiente");
        }
    }

    /**
     * Obtiene todas las solicitudes de adopción en estado pendiente (uso exclusivo de ADMIN).
     *
     * @return lista de {@link SolicitudAdopcionDTO} pendientes, o 204 si no hay ninguna
     */
    @Operation(summary = "Obtener solicitudes pendientes (ADMIN)")
    @GetMapping("/pendientes")
    public ResponseEntity<?> getPendientes() {
        List<SolicitudAdopcionDTO> list = solicitudService.obtenerPendientes();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene las solicitudes de adopción del usuario autenticado.
     *
     * @return lista de {@link SolicitudAdopcionDTO} del usuario, 204 si no tiene solicitudes,
     *         o 404 si el usuario no es encontrado
     */
    @Operation(summary = "Mis solicitudes (usuario autenticado)")
    @GetMapping("/missolicitudes")
    public ResponseEntity<?> misSolicitudes() {
        try {
            Long adopterId = solicitudService.obtenerIdPorUsername(getUsuarioActual());
            List<SolicitudAdopcionDTO> list = solicitudService.obtenerPorAdoptante(adopterId);
            if (list.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(list);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    /**
     * Obtiene todas las solicitudes de adopción asociadas a un animal específico.
     *
     * @param animalId identificador del animal
     * @return lista de {@link SolicitudAdopcionDTO} del animal, o 204 si no hay ninguna
     */
    @Operation(summary = "Solicitudes por animal")
    @GetMapping("/animal/{animalId}")
    public ResponseEntity<?> getByAnimal(@PathVariable Long animalId) {
        List<SolicitudAdopcionDTO> list = solicitudService.obtenerPorAnimal(animalId);
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }
}