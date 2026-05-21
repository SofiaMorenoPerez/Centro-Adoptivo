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

@Entity
@Table(name = "adoption_request")
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private User adopter;

    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String rejectionReason;

    private LocalDateTime resolutionDate;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public SolicitudAdopcion() {}

    public SolicitudAdopcion(Animal animal, User adopter,
            LocalDateTime requestDate, RequestStatus status) {
        this.animal = animal;
        this.adopter = adopter;
        this.requestDate = requestDate;
        this.status = status;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public User getAdopter() {
		return adopter;
	}

	public void setAdopter(User adopter) {
		this.adopter = adopter;
	}

	public LocalDateTime getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public LocalDateTime getResolutionDate() {
		return resolutionDate;
	}

	public void setResolutionDate(LocalDateTime resolutionDate) {
		this.resolutionDate = resolutionDate;
	}

	@Override
    public String toString() {
        return "AdoptionRequest [id=" + id
                + ", animal=" + animal.getName()
                + ", adopter=" + adopter.getUsername()
                + ", requestDate=" + requestDate
                + ", status=" + status + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SolicitudAdopcion other = (SolicitudAdopcion) obj;
        return Objects.equals(id, other.id);
    }
}