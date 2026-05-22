package co.edu.unbosque.centroadoptivo.dto;

import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalAge;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalClassification;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import java.time.LocalDateTime;

public class AnimalDTO {

    private Long id;
    private String name;
    private AnimalAge age;
    private boolean sterilized;
    private boolean vaccinated;
    private String species;
    private String breed;
    private String color;
    private String observations;
    private String image;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private AnimalClassification classification;
    private AnimalStatus status;
    private Long publisherId;
    private Long adopterId;

    public AnimalDTO() {}

    public AnimalDTO(Long id, String name, AnimalAge age, boolean sterilized,
            boolean vaccinated, String species, String breed, String color,
            String observations, String image, LocalDateTime publishedAt,
            LocalDateTime updatedAt, AnimalClassification classification,
            AnimalStatus status, Long publisherId, Long adopterId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sterilized = sterilized;
        this.vaccinated = vaccinated;
        this.species = species;
        this.breed = breed;
        this.color = color;
        this.observations = observations;
        this.image = image;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.classification = classification;
        this.status = status;
        this.publisherId = publisherId;
        this.adopterId = adopterId;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AnimalAge getAge() {
		return age;
	}

	public void setAge(AnimalAge age) {
		this.age = age;
	}

	public boolean isSterilized() {
		return sterilized;
	}

	public void setSterilized(boolean sterilized) {
		this.sterilized = sterilized;
	}

	public boolean isVaccinated() {
		return vaccinated;
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public AnimalClassification getClassification() {
		return classification;
	}

	public void setClassification(AnimalClassification classification) {
		this.classification = classification;
	}

	public AnimalStatus getStatus() {
		return status;
	}

	public void setStatus(AnimalStatus status) {
		this.status = status;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public Long getAdopterId() {
		return adopterId;
	}

	public void setAdopterId(Long adopterId) {
		this.adopterId = adopterId;
	}

    
}