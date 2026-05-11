package co.edu.unbosque.centroadoptivo.dto;

import java.util.Objects;
import java.time.LocalDateTime;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalClasificacion;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalEdad;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalEstado;

/**
 * DTO que representa un animal registrado en el sistema
 * del centro de adopción. Contiene la información básica del animal,
 * los atributos detectados por inteligencia artificial y su estado
 * dentro del proceso de adopción.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class AnimalDTO {

    /** Identificador único del animal. */
    private long id;

    /** Nombre del animal ingresado manualmente por el usuario publicador. */
    private String nombre;

    /**
     * Etapa de vida del animal estimada por inteligencia artificial.
     * @see AnimalEdad
     */
    private AnimalEdad edad;

    /** Indica si el animal ha sido esterilizado. */
    private boolean esterilizado;

    /** Indica si el animal cuenta con vacunas aplicadas. */
    private boolean vacunado;

    /** Especie del animal detectada por inteligencia artificial. */
    private String especie;

    /** Raza del animal detectada por inteligencia artificial. */
    private String raza;

    /** Color o colores predominantes detectados por inteligencia artificial. */
    private String color;

    /** Observaciones adicionales ingresadas por el usuario publicador. */
    private String observaciones;

    /** URL de la imagen del animal. */
    private String imagen;

    /** Fecha y hora en que el animal fue registrado en el sistema. */
    private LocalDateTime publicadoEn;

    /** Fecha y hora de la última actualización del registro. */
    private LocalDateTime actualizadoEn;

    /**
     * Clasificación del animal determinada por inteligencia artificial.
     * @see AnimalClasificacion
     */
    private AnimalClasificacion clasificacion;

    /**
     * Estado actual del animal dentro del proceso de adopción.
     * @see AnimalEstado
     */
    private AnimalEstado estado;

    /** Constructor vacío. */
    public AnimalDTO() {
    }

    /**
     * Constructor completo para crear una instancia de AnimalDTO.
     *
     * @param nombre         Nombre del animal ingresado por el usuario
     * @param edad           Etapa de vida estimada por IA
     * @param esterilizado   Indica si está esterilizado
     * @param vacunado       Indica si está vacunado
     * @param especie        Especie detectada por IA
     * @param raza           Raza detectada por IA
     * @param color          Color detectado por IA
     * @param observaciones  Observaciones ingresadas por el usuario
     * @param imagen         URL de la imagen del animal
     * @param publicadoEn    Fecha de publicación
     * @param actualizadoEn  Fecha de última actualización
     * @param clasificacion  Clasificación detectada por IA
     * @param estado         Estado en el proceso de adopción
     */
    public AnimalDTO(String nombre, AnimalEdad edad, boolean esterilizado, boolean vacunado,
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

    /**
     * Obtiene el identificador único del animal.
     * @return id del animal
     */
    public long getId() { return id; }

    /**
     * Establece el identificador único del animal.
     * @param id identificador a asignar
     */
    public void setId(long id) { this.id = id; }

    /**
     * Obtiene el nombre del animal.
     * @return nombre del animal
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del animal.
     * @param nombre nombre a asignar
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la etapa de vida del animal.
     * @return edad del animal
     */
    public AnimalEdad getEdad() { return edad; }

    /**
     * Establece la etapa de vida del animal.
     * @param edad etapa de vida a asignar
     */
    public void setEdad(AnimalEdad edad) { this.edad = edad; }

    /**
     * Indica si el animal está esterilizado.
     * @return true si está esterilizado
     */
    public boolean isEsterilizado() { return esterilizado; }

    /**
     * Establece si el animal está esterilizado.
     * @param esterilizado valor a asignar
     */
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    /**
     * Indica si el animal está vacunado.
     * @return true si está vacunado
     */
    public boolean isVacunado() { return vacunado; }

    /**
     * Establece si el animal está vacunado.
     * @param vacunado valor a asignar
     */
    public void setVacunado(boolean vacunado) { this.vacunado = vacunado; }

    /**
     * Obtiene la especie del animal.
     * @return especie del animal
     */
    public String getEspecie() { return especie; }

    /**
     * Establece la especie del animal.
     * @param especie especie a asignar
     */
    public void setEspecie(String especie) { this.especie = especie; }

    /**
     * Obtiene la raza del animal.
     * @return raza del animal
     */
    public String getRaza() { return raza; }

    /**
     * Establece la raza del animal.
     * @param raza raza a asignar
     */
    public void setRaza(String raza) { this.raza = raza; }

    /**
     * Obtiene el color predominante del animal.
     * @return color del animal
     */
    public String getColor() { return color; }

    /**
     * Establece el color predominante del animal.
     * @param color color a asignar
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Obtiene las observaciones del animal.
     * @return observaciones del animal
     */
    public String getObservaciones() { return observaciones; }

    /**
     * Establece las observaciones del animal.
     * @param observaciones observaciones a asignar
     */
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    /**
     * Obtiene la URL de la imagen del animal.
     * @return URL de la imagen
     */
    public String getImagen() { return imagen; }

    /**
     * Establece la URL de la imagen del animal.
     * @param imagen URL a asignar
     */
    public void setImagen(String imagen) { this.imagen = imagen; }

    /**
     * Obtiene la fecha de publicación del animal.
     * @return fecha de publicación
     */
    public LocalDateTime getPublicadoEn() { return publicadoEn; }

    /**
     * Establece la fecha de publicación del animal.
     * @param publicadoEn fecha a asignar
     */
    public void setPublicadoEn(LocalDateTime publicadoEn) { this.publicadoEn = publicadoEn; }

    /**
     * Obtiene la fecha de última actualización del animal.
     * @return fecha de actualización
     */
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    /**
     * Establece la fecha de última actualización del animal.
     * @param actualizadoEn fecha a asignar
     */
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    /**
     * Obtiene la clasificación del animal.
     * @return clasificación del animal
     */
    public AnimalClasificacion getClasificacion() { return clasificacion; }

    /**
     * Establece la clasificación del animal.
     * @param clasificacion clasificación a asignar
     */
    public void setClasificacion(AnimalClasificacion clasificacion) { this.clasificacion = clasificacion; }

    /**
     * Obtiene el estado actual del animal en el proceso de adopción.
     * @return estado del animal
     */
    public AnimalEstado getEstado() { return estado; }

    /**
     * Establece el estado del animal en el proceso de adopción.
     * @param estado estado a asignar
     */
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
                && edad == other.edad
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