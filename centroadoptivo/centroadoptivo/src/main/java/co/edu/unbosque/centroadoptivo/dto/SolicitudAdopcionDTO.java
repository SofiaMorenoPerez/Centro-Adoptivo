package co.edu.unbosque.centroadoptivo.dto;

import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad SolicitudAdopcion.
 * <p>
 * Se utiliza para transferir datos de solicitudes de adopción entre las capas
 * de la aplicación sin exponer directamente la entidad de persistencia.
 * </p>
 */
public class SolicitudAdopcionDTO {

    /** Identificador único de la solicitud. */
    private Long id;

    /** Identificador del animal al que corresponde la solicitud. */
    private Long animalId;

    /** Nombre del animal al que corresponde la solicitud. */
    private String animalName;

    /** Identificador del usuario adoptante que realizó la solicitud. */
    private Long adopterId;

    /** Nombre de usuario del adoptante que realizó la solicitud. */
    private String adopterUsername;

    /** Fecha y hora en que se realizó la solicitud. */
    private LocalDateTime requestDate;

    /** Estado actual de la solicitud. */
    private RequestStatus status;

    /** Motivo de rechazo de la solicitud, si aplica. */
    private String rejectionReason;

    /** Fecha y hora en que fue resuelta la solicitud. */
    private LocalDateTime resolutionDate;

    /** Constructor por defecto. */
    public SolicitudAdopcionDTO() {}

    /**
     * Constructor con todos los campos del DTO.
     *
     * @param id              identificador único de la solicitud
     * @param animalId        identificador del animal
     * @param animalName      nombre del animal
     * @param adopterId       identificador del adoptante
     * @param adopterUsername nombre de usuario del adoptante
     * @param requestDate     fecha y hora de la solicitud
     * @param status          estado actual de la solicitud
     * @param rejectionReason motivo de rechazo, si aplica
     * @param resolutionDate  fecha y hora de resolución
     */
    public SolicitudAdopcionDTO(Long id, Long animalId, String animalName,
            Long adopterId, String adopterUsername, LocalDateTime requestDate,
            RequestStatus status, String rejectionReason, LocalDateTime resolutionDate) {
        this.id = id;
        this.animalId = animalId;
        this.animalName = animalName;
        this.adopterId = adopterId;
        this.adopterUsername = adopterUsername;
        this.requestDate = requestDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.resolutionDate = resolutionDate;
    }

    /**
     * Retorna el identificador único de la solicitud.
     *
     * @return id de la solicitud
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la solicitud.
     *
     * @param id id de la solicitud
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el identificador del animal al que corresponde la solicitud.
     *
     * @return id del animal
     */
    public Long getAnimalId() {
        return animalId;
    }

    /**
     * Establece el identificador del animal al que corresponde la solicitud.
     *
     * @param animalId id del animal
     */
    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    /**
     * Retorna el nombre del animal al que corresponde la solicitud.
     *
     * @return nombre del animal
     */
    public String getAnimalName() {
        return animalName;
    }

    /**
     * Establece el nombre del animal al que corresponde la solicitud.
     *
     * @param animalName nombre del animal
     */
    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    /**
     * Retorna el identificador del usuario adoptante.
     *
     * @return id del adoptante
     */
    public Long getAdopterId() {
        return adopterId;
    }

    /**
     * Establece el identificador del usuario adoptante.
     *
     * @param adopterId id del adoptante
     */
    public void setAdopterId(Long adopterId) {
        this.adopterId = adopterId;
    }

    /**
     * Retorna el nombre de usuario del adoptante.
     *
     * @return username del adoptante
     */
    public String getAdopterUsername() {
        return adopterUsername;
    }

    /**
     * Establece el nombre de usuario del adoptante.
     *
     * @param adopterUsername username del adoptante
     */
    public void setAdopterUsername(String adopterUsername) {
        this.adopterUsername = adopterUsername;
    }

    /**
     * Retorna la fecha y hora en que se realizó la solicitud.
     *
     * @return fecha de solicitud
     */
    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    /**
     * Establece la fecha y hora en que se realizó la solicitud.
     *
     * @param requestDate fecha de solicitud
     */
    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    /**
     * Retorna el estado actual de la solicitud.
     *
     * @return estado de la solicitud
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Establece el estado actual de la solicitud.
     *
     * @param status estado de la solicitud
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * Retorna el motivo de rechazo de la solicitud.
     *
     * @return motivo de rechazo, o {@code null} si no fue rechazada
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Establece el motivo de rechazo de la solicitud.
     *
     * @param rejectionReason motivo de rechazo
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * Retorna la fecha y hora en que fue resuelta la solicitud.
     *
     * @return fecha de resolución, o {@code null} si aún está pendiente
     */
    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    /**
     * Establece la fecha y hora en que fue resuelta la solicitud.
     *
     * @param resolutionDate fecha de resolución
     */
    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }
}