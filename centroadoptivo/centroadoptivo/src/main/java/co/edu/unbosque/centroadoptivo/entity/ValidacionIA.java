package co.edu.unbosque.centroadoptivo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa el resultado de una validación realizada por inteligencias
 * artificiales sobre un animal registrado en el centro adoptivo.
 * <p>
 * Almacena el consenso obtenido entre múltiples IAs, el detalle de la evaluación
 * y la referencia al animal validado.
 * </p>
 */
@Entity
@Table(name = "validacion_ia")
public class ValidacionIA {

    /** Identificador único autogenerado de la validación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Indica si la validación fue aprobada por mayoría de IAs. */
    private boolean aprobado;

    /** Número de votos a favor emitidos por las IAs. */
    private int votos;

    /** Número total de IAs que participaron en la validación. */
    private int totalIAs;

    /** Detalle descriptivo del resultado de la validación (máximo 1000 caracteres). */
    @Column(length = 1000)
    private String detalle;

    /** Fecha y hora en que se realizó la validación. */
    private LocalDateTime fechaValidacion;

    /** Animal al que pertenece esta validación. */
    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public ValidacionIA() {}

    /**
     * Constructor completo para crear una validación con todos sus datos.
     *
     * @param aprobado        {@code true} si la validación fue aprobada
     * @param votos           número de votos a favor
     * @param totalIAs        total de IAs que participaron
     * @param detalle         descripción del resultado de la validación
     * @param fechaValidacion fecha y hora de la validación
     * @param animal          animal asociado a esta validación
     */
    public ValidacionIA(boolean aprobado, int votos, int totalIAs,
            String detalle, LocalDateTime fechaValidacion, Animal animal) {
        this.aprobado = aprobado;
        this.votos = votos;
        this.totalIAs = totalIAs;
        this.detalle = detalle;
        this.fechaValidacion = fechaValidacion;
        this.animal = animal;
    }

    /**
     * Retorna el identificador único de la validación.
     *
     * @return id de la validación
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único de la validación.
     *
     * @param id nuevo id de la validación
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Indica si la validación fue aprobada.
     *
     * @return {@code true} si fue aprobada por las IAs
     */
    public boolean isAprobado() { return aprobado; }

    /**
     * Establece el resultado de aprobación de la validación.
     *
     * @param aprobado {@code true} si la validación es aprobada
     */
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    /**
     * Retorna el número de votos a favor emitidos por las IAs.
     *
     * @return cantidad de votos a favor
     */
    public int getVotos() { return votos; }

    /**
     * Establece el número de votos a favor de la validación.
     *
     * @param votos nuevos votos a favor
     */
    public void setVotos(int votos) { this.votos = votos; }

    /**
     * Retorna el total de IAs que participaron en la validación.
     *
     * @return total de IAs participantes
     */
    public int getTotalIAs() { return totalIAs; }

    /**
     * Establece el total de IAs que participaron en la validación.
     *
     * @param totalIAs nuevo total de IAs
     */
    public void setTotalIAs(int totalIAs) { this.totalIAs = totalIAs; }

    /**
     * Retorna el detalle descriptivo del resultado de la validación.
     *
     * @return detalle de la validación
     */
    public String getDetalle() { return detalle; }

    /**
     * Establece el detalle descriptivo del resultado de la validación.
     *
     * @param detalle nuevo detalle de la validación
     */
    public void setDetalle(String detalle) { this.detalle = detalle; }

    /**
     * Retorna la fecha y hora en que se realizó la validación.
     *
     * @return fecha de validación
     */
    public LocalDateTime getFechaValidacion() { return fechaValidacion; }

    /**
     * Establece la fecha y hora en que se realizó la validación.
     *
     * @param fechaValidacion nueva fecha de validación
     */
    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    /**
     * Retorna el animal asociado a esta validación.
     *
     * @return animal validado
     */
    public Animal getAnimal() { return animal; }

    /**
     * Establece el animal asociado a esta validación.
     *
     * @param animal nuevo animal a asociar
     */
    public void setAnimal(Animal animal) { this.animal = animal; }

    /**
     * Retorna una representación en cadena de la validación con sus datos principales.
     *
     * @return cadena con id, aprobado, votos, totalIAs, detalle y fechaValidacion
     */
    @Override
    public String toString() {
        return "ValidacionIA [id=" + id + ", aprobado=" + aprobado
                + ", votos=" + votos + ", totalIAs=" + totalIAs
                + ", detalle=" + detalle + ", fechaValidacion=" + fechaValidacion + "]";
    }

    /**
     * Calcula el hash de la validación basado en todos sus campos relevantes.
     *
     * @return valor hash de la validación
     */
    @Override
    public int hashCode() {
        return Objects.hash(aprobado, detalle, fechaValidacion, id, totalIAs, votos);
    }

    /**
     * Compara esta validación con otro objeto para determinar igualdad.
     * <p>
     * Dos validaciones son iguales si todos sus campos relevantes coinciden:
     * {@code aprobado}, {@code detalle}, {@code fechaValidacion}, {@code id},
     * {@code totalIAs} y {@code votos}.
     * </p>
     *
     * @param obj objeto a comparar
     * @return {@code true} si todos los campos relevantes son iguales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ValidacionIA other = (ValidacionIA) obj;
        return aprobado == other.aprobado
                && Objects.equals(detalle, other.detalle)
                && Objects.equals(fechaValidacion, other.fechaValidacion)
                && Objects.equals(id, other.id)
                && totalIAs == other.totalIAs
                && votos == other.votos;
    }
}