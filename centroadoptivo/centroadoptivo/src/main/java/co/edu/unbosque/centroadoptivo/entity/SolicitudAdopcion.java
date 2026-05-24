package co.edu.unbosque.centroadoptivo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa una solicitud de adopción en el sistema.
 * <p>
 * Almacena la relación entre un animal y un usuario adoptante,
 * junto con el estado de la solicitud y su historial de resolución.
 * </p>
 */
@Entity
@Table(name = "adoption_request")
public class SolicitudAdopcion {

    /** Identificador único de la solicitud, generado automáticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Animal al que corresponde la solicitud de adopción. */
    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    /** Usuario adoptante que realizó la solicitud. */
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private User adopter;

    /** Fecha y hora en que se realizó la solicitud. */
    private LocalDateTime requestDate;

    /** Estado actual de la solicitud. */
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    /** Motivo de rechazo de la solicitud, si aplica. */
    private String rejectionReason;

    /** Fecha y hora en que fue resuelta la solicitud. */
    private LocalDateTime resolutionDate;

    /**
     * Enumeración que representa el estado de una solicitud de adopción.
     */
    public enum RequestStatus {
        /** Solicitud pendiente de revisión. */
        PENDING,
        /** Solicitud aprobada. */
        APPROVED,
        /** Solicitud rechazada. */
        REJECTED
    }

    /** Constructor por defecto. */
    public SolicitudAdopcion() {}

    /**
     * Constructor con los campos principales de la solicitud.
     *
     * @param animal      animal al que corresponde la solicitud
     * @param adopter     usuario adoptante que realizó la solicitud
     * @param requestDate fecha y hora de la solicitud
     * @param status      estado inicial de la solicitud
     */
    public SolicitudAdopcion(Animal animal, User adopter,
            LocalDateTime requestDate, RequestStatus status) {
        this.animal = animal;
        this.adopter = adopter;
        this.requestDate = requestDate;
        this.status = status;
    }

    /**
     * Retorna el identificador único de la solicitud.
     *
     * @return id de la solicitud
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único de la solicitud.
     *
     * @param id id de la solicitud
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna el animal al que corresponde la solicitud.
     *
     * @return animal de la solicitud
     */
    public Animal getAnimal() { return animal; }

    /**
     * Establece el animal al que corresponde la solicitud.
     *
     * @param animal animal de la solicitud
     */
    public void setAnimal(Animal animal) { this.animal = animal; }

    /**
     * Retorna el usuario adoptante que realizó la solicitud.
     *
     * @return usuario adoptante
     */
    public User getAdopter() { return adopter; }

    /**
     * Establece el usuario adoptante que realizó la solicitud.
     *
     * @param adopter usuario adoptante
     */
    public void setAdopter(User adopter) { this.adopter = adopter; }

    /**
     * Retorna la fecha y hora en que se realizó la solicitud.
     *
     * @return fecha de la solicitud
     */
    public LocalDateTime getRequestDate() { return requestDate; }

    /**
     * Establece la fecha y hora en que se realizó la solicitud.
     *
     * @param requestDate fecha de la solicitud
     */
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    /**
     * Retorna el estado actual de la solicitud.
     *
     * @return estado de la solicitud
     */
    public RequestStatus getStatus() { return status; }

    /**
     * Establece el estado actual de la solicitud.
     *
     * @param status estado de la solicitud
     */
    public void setStatus(RequestStatus status) { this.status = status; }

    /**
     * Retorna el motivo de rechazo de la solicitud.
     *
     * @return motivo de rechazo, o {@code null} si no fue rechazada
     */
    public String getRejectionReason() { return rejectionReason; }

    /**
     * Establece el motivo de rechazo de la solicitud.
     *
     * @param rejectionReason motivo de rechazo
     */
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    /**
     * Retorna la fecha y hora en que fue resuelta la solicitud.
     *
     * @return fecha de resolución, o {@code null} si aún está pendiente
     */
    public LocalDateTime getResolutionDate() { return resolutionDate; }

    /**
     * Establece la fecha y hora en que fue resuelta la solicitud.
     *
     * @param resolutionDate fecha de resolución
     */
    public void setResolutionDate(LocalDateTime resolutionDate) { this.resolutionDate = resolutionDate; }

    /**
     * Retorna una representación en cadena de la solicitud con sus campos principales.
     *
     * @return cadena con id, nombre del animal, username del adoptante, fecha y estado
     */
    @Override
    public String toString() {
        return "AdoptionRequest [id=" + id
                + ", animal=" + animal.getName()
                + ", adopter=" + adopter.getUsername()
                + ", requestDate=" + requestDate
                + ", status=" + status + "]";
    }

    /**
     * Retorna el código hash basado en el identificador de la solicitud.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara esta solicitud con otro objeto por su identificador.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos tienen el mismo id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SolicitudAdopcion other = (SolicitudAdopcion) obj;
        return Objects.equals(id, other.id);
    }
}