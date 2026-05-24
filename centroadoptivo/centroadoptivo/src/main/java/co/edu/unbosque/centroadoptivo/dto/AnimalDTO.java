package co.edu.unbosque.centroadoptivo.dto;

import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalAge;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalClassification;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Animal.
 * <p>
 * Se utiliza para transferir datos de animales entre las capas de la aplicación
 * sin exponer directamente la entidad de persistencia.
 * </p>
 */
public class AnimalDTO {

    /** Identificador único del animal. */
    private Long id;

    /** Nombre del animal. */
    private String name;

    /** Rango de edad del animal. */
    private AnimalAge age;

    /** Indica si el animal está esterilizado. */
    private boolean sterilized;

    /** Indica si el animal está vacunado. */
    private boolean vaccinated;

    /** Especie del animal. */
    private String species;

    /** Raza del animal. */
    private String breed;

    /** Color del animal. */
    private String color;

    /** Observaciones adicionales sobre el animal. */
    private String observations;

    /** Ruta o URL de la imagen del animal. */
    private String image;

    /** Fecha y hora en que fue publicado el animal. */
    private LocalDateTime publishedAt;

    /** Fecha y hora de la última actualización del registro. */
    private LocalDateTime updatedAt;

    /** Clasificación del animal según criterios del sistema. */
    private AnimalClassification classification;

    /** Estado actual del animal en el proceso de adopción. */
    private AnimalStatus status;

    /** Identificador del usuario que publicó el animal. */
    private Long publisherId;

    /** Identificador del usuario que adoptó el animal. */
    private Long adopterId;

    /** Constructor por defecto. */
    public AnimalDTO() {}

    /**
     * Constructor con todos los campos del DTO.
     *
     * @param id             identificador único del animal
     * @param name           nombre del animal
     * @param age            rango de edad del animal
     * @param sterilized     indica si está esterilizado
     * @param vaccinated     indica si está vacunado
     * @param species        especie del animal
     * @param breed          raza del animal
     * @param color          color del animal
     * @param observations   observaciones adicionales
     * @param image          ruta o URL de la imagen
     * @param publishedAt    fecha y hora de publicación
     * @param updatedAt      fecha y hora de última actualización
     * @param classification clasificación del animal
     * @param status         estado actual en el proceso de adopción
     * @param publisherId    identificador del usuario publicador
     * @param adopterId      identificador del usuario adoptante
     */
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

    /**
     * Retorna el identificador único del animal.
     *
     * @return id del animal
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del animal.
     *
     * @param id id del animal
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el nombre del animal.
     *
     * @return nombre del animal
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del animal.
     *
     * @param name nombre del animal
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna el rango de edad del animal.
     *
     * @return edad del animal
     */
    public AnimalAge getAge() {
        return age;
    }

    /**
     * Establece el rango de edad del animal.
     *
     * @param age edad del animal
     */
    public void setAge(AnimalAge age) {
        this.age = age;
    }

    /**
     * Indica si el animal está esterilizado.
     *
     * @return {@code true} si está esterilizado
     */
    public boolean isSterilized() {
        return sterilized;
    }

    /**
     * Establece si el animal está esterilizado.
     *
     * @param sterilized {@code true} si está esterilizado
     */
    public void setSterilized(boolean sterilized) {
        this.sterilized = sterilized;
    }

    /**
     * Indica si el animal está vacunado.
     *
     * @return {@code true} si está vacunado
     */
    public boolean isVaccinated() {
        return vaccinated;
    }

    /**
     * Establece si el animal está vacunado.
     *
     * @param vaccinated {@code true} si está vacunado
     */
    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    /**
     * Retorna la especie del animal.
     *
     * @return especie del animal
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Establece la especie del animal.
     *
     * @param species especie del animal
     */
    public void setSpecies(String species) {
        this.species = species;
    }

    /**
     * Retorna la raza del animal.
     *
     * @return raza del animal
     */
    public String getBreed() {
        return breed;
    }

    /**
     * Establece la raza del animal.
     *
     * @param breed raza del animal
     */
    public void setBreed(String breed) {
        this.breed = breed;
    }

    /**
     * Retorna el color del animal.
     *
     * @return color del animal
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color del animal.
     *
     * @param color color del animal
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Retorna las observaciones adicionales sobre el animal.
     *
     * @return observaciones del animal
     */
    public String getObservations() {
        return observations;
    }

    /**
     * Establece las observaciones adicionales sobre el animal.
     *
     * @param observations observaciones del animal
     */
    public void setObservations(String observations) {
        this.observations = observations;
    }

    /**
     * Retorna la ruta o URL de la imagen del animal.
     *
     * @return imagen del animal
     */
    public String getImage() {
        return image;
    }

    /**
     * Establece la ruta o URL de la imagen del animal.
     *
     * @param image imagen del animal
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Retorna la fecha y hora de publicación del animal.
     *
     * @return fecha de publicación
     */
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    /**
     * Establece la fecha y hora de publicación del animal.
     *
     * @param publishedAt fecha de publicación
     */
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * Retorna la fecha y hora de la última actualización del registro.
     *
     * @return fecha de última actualización
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Establece la fecha y hora de la última actualización del registro.
     *
     * @param updatedAt fecha de última actualización
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Retorna la clasificación del animal.
     *
     * @return clasificación del animal
     */
    public AnimalClassification getClassification() {
        return classification;
    }

    /**
     * Establece la clasificación del animal.
     *
     * @param classification clasificación del animal
     */
    public void setClassification(AnimalClassification classification) {
        this.classification = classification;
    }

    /**
     * Retorna el estado actual del animal en el proceso de adopción.
     *
     * @return estado del animal
     */
    public AnimalStatus getStatus() {
        return status;
    }

    /**
     * Establece el estado actual del animal en el proceso de adopción.
     *
     * @param status estado del animal
     */
    public void setStatus(AnimalStatus status) {
        this.status = status;
    }

    /**
     * Retorna el identificador del usuario que publicó el animal.
     *
     * @return id del publicador
     */
    public Long getPublisherId() {
        return publisherId;
    }

    /**
     * Establece el identificador del usuario que publicó el animal.
     *
     * @param publisherId id del publicador
     */
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    /**
     * Retorna el identificador del usuario que adoptó el animal.
     *
     * @return id del adoptante
     */
    public Long getAdopterId() {
        return adopterId;
    }

    /**
     * Establece el identificador del usuario que adoptó el animal.
     *
     * @param adopterId id del adoptante
     */
    public void setAdopterId(Long adopterId) {
        this.adopterId = adopterId;
    }
}