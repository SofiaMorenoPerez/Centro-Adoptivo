package co.edu.unbosque.centroadoptivo.dto;

import java.util.Objects;
import java.time.LocalDateTime;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalClasificacion;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalEstado;

/**
 * DTO que representa un animal registrado en el sistema
 * del centro de adopción. Contiene la información básica del animal,
 * los atributos detectados por inteligencia artificial y su estado
 * dentro del proceso de adopción.
 */
public class AnimalDTO {

    private long id;
    private String nombre;
    private String edad;
    private boolean esterilizado;
    private boolean vacunado;
    private String especie;
    private String raza;
    private String color;
    private String observaciones;
    private String imagen;
    private LocalDateTime publicadoEn;
    private LocalDateTime actualizadoEn;
    private AnimalClasificacion clasificacion;
    private AnimalEstado estado;

    public AnimalDTO() {
    }

    /**
     * Constructor que inicializa los campos principales del animal.
     *
     * @param nombre         nombre del animal ingresado por el usuario
     * @param edad           edad del animal ingresada por el usuario
     * @param esterilizado   indica si el animal está esterilizado
     * @param vacunado       indica si el animal está vacunado
     * @param especie        especie detectada por inteligencia artificial
     * @param raza           raza detectada por inteligencia artificial
     * @param color          color detectado por inteligencia artificial
     * @param observaciones  observaciones ingresadas por el usuario
     * @param imagen         URL de la imagen del animal
     * @param publicadoEn    fecha de publicación del animal
     * @param actualizadoEn  fecha de última actualización
     * @param clasificacion  clasificación detectada por inteligencia artificial
     * @param estado         estado actual en el proceso de adopción
     */
    public AnimalDTO(String nombre, String edad, boolean esterilizado, boolean vacunado,
            String especie, String raza, String color, String observaciones, String imagen,
            LocalDateTime publicadoEn, LocalDateTime actualizadoEn,
            AnimalClasificacion clasificacion, AnimalEstado estado) {
        this.nombre = nombre;
        this.edad = edad;
        this.esterilizado = esterilizado;
        this.vacunado = vacunado;
        this.especie = especie;
        this.raza = raza;
        this.color = color;
        this.observaciones = observaciones;
        this.imagen = imagen;
        this.publicadoEn = publicadoEn;
        this.actualizadoEn = actualizadoEn;
        this.clasificacion = clasificacion;
        this.estado = estado;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEdad() { return edad; }
    public void setEdad(String edad) { this.edad = edad; }

    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    public boolean isVacunado() { return vacunado; }
    public void setVacunado(boolean vacunado) { this.vacunado = vacunado; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public LocalDateTime getPublicadoEn() { return publicadoEn; }
    public void setPublicadoEn(LocalDateTime publicadoEn) { this.publicadoEn = publicadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    public AnimalClasificacion getClasificacion() { return clasificacion; }
    public void setClasificacion(AnimalClasificacion clasificacion) { this.clasificacion = clasificacion; }

    public AnimalEstado getEstado() { return estado; }
    public void setEstado(AnimalEstado estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "AnimalDTO [id=" + id + ", nombre=" + nombre + ", edad=" + edad
                + ", esterilizado=" + esterilizado + ", vacunado=" + vacunado
                + ", especie=" + especie + ", raza=" + raza + ", color=" + color
                + ", observaciones=" + observaciones + ", imagen=" + imagen
                + ", publicadoEn=" + publicadoEn + ", actualizadoEn=" + actualizadoEn
                + ", clasificacion=" + clasificacion + ", estado=" + estado + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(actualizadoEn, clasificacion, color, edad, especie,
                estado, esterilizado, id, imagen, nombre, observaciones,
                publicadoEn, raza, vacunado);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AnimalDTO other = (AnimalDTO) obj;
        return Objects.equals(actualizadoEn, other.actualizadoEn)
                && clasificacion == other.clasificacion
                && Objects.equals(color, other.color)
                && Objects.equals(edad, other.edad)
                && Objects.equals(especie, other.especie)
                && estado == other.estado
                && esterilizado == other.esterilizado
                && id == other.id
                && Objects.equals(imagen, other.imagen)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(observaciones, other.observaciones)
                && Objects.equals(publicadoEn, other.publicadoEn)
                && Objects.equals(raza, other.raza)
                && vacunado == other.vacunado;
    }
}