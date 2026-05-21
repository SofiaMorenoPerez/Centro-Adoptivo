package co.edu.unbosque.centroadoptivo.dto;

import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import java.time.LocalDateTime;

public class SolicitudAdopcionDTO {

    private Long id;
    private Long animalId;
    private String animalName;
    private Long adopterId;
    private String adopterUsername;
    private LocalDateTime requestDate;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime resolutionDate;

    public SolicitudAdopcionDTO() {}

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAnimalId() {
		return animalId;
	}

	public void setAnimalId(Long animalId) {
		this.animalId = animalId;
	}

	public String getAnimalName() {
		return animalName;
	}

	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}

	public Long getAdopterId() {
		return adopterId;
	}

	public void setAdopterId(Long adopterId) {
		this.adopterId = adopterId;
	}

	public String getAdopterUsername() {
		return adopterUsername;
	}

	public void setAdopterUsername(String adopterUsername) {
		this.adopterUsername = adopterUsername;
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
    
}