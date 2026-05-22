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
@Table(name = "animal")
public class Animal {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private String name;
    private @Enumerated(EnumType.STRING) AnimalAge age;
    private boolean sterilized;
    private boolean vaccinated;
    private String species;
    private String breed;
    private String color;
    private String observations;
    private String image;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private @Enumerated(EnumType.STRING) AnimalClassification classification;
    private @Enumerated(EnumType.STRING) AnimalStatus status;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private User publisher;

    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private User adopter;

    public enum AnimalAge {
        PUPPY,
        YOUNG,
        ADULT,
        SENIOR
    }

    public enum AnimalClassification {
        DOMESTIC,
        NON_DOMESTIC
    }

    public enum AnimalStatus {
        AVAILABLE,
        PENDING,
        ADOPTED
    }

    public Animal() {}

    public Animal(String name, AnimalAge age, boolean sterilized, boolean vaccinated,
            String species, String breed, String color, String observations, String image,
            LocalDateTime publishedAt, LocalDateTime updatedAt,
            AnimalClassification classification, AnimalStatus status,
            User publisher, User adopter) {
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
        this.publisher = publisher;
        this.adopter = adopter;
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

	public User getPublisher() {
		return publisher;
	}

	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}

	public User getAdopter() {
		return adopter;
	}

	public void setAdopter(User adopter) {
		this.adopter = adopter;
	}

	@Override
    public String toString() {
        return "Animal [id=" + id
                + ", name=" + name
                + ", species=" + species
                + ", breed=" + breed
                + ", status=" + status + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Animal other = (Animal) obj;
        return Objects.equals(id, other.id);
    }
}