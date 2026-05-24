package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) para el resultado de validación de un animal por IA.
 * <p>
 * Representa el registro persistido de una validación realizada por el servicio
 * de inteligencia artificial, incluyendo el resultado, los votos y la fecha de validación.
 * </p>
 */
public class ValidacionIADTO {

    /** Identificador único del registro de validación. */
    private Long id;

    /** Indica si el animal fue aprobado por la mayoría de IAs consultadas. */
    private boolean aprobado;

    /** Número de IAs que aprobaron el registro del animal. */
    private int votos;

    /** Total de IAs consultadas durante la validación. */
    private int totalIAs;

    /** Detalle adicional sobre el resultado de la validación. */
    private String detalle;

    /** Fecha y hora en que se realizó la validación. */
    private LocalDateTime fechaValidacion;

    /** Identificador del animal al que corresponde esta validación. */
    private Long animalId;

    /** Constructor por defecto. */
    public ValidacionIADTO() {}

    /**
     * Constructor con los campos principales del resultado de validación.
     *
     * @param aprobado         {@code true} si el animal fue aprobado
     * @param votos            número de IAs que aprobaron
     * @param totalIAs         total de IAs consultadas
     * @param detalle          detalle adicional del resultado
     * @param fechaValidacion  fecha y hora de la validación
     * @param animalId         identificador del animal validado
     */
    public ValidacionIADTO(boolean aprobado, int votos, int totalIAs, String detalle,
            LocalDateTime fechaValidacion, Long animalId) {
        super();
        this.aprobado = aprobado;
        this.votos = votos;
        this.totalIAs = totalIAs;
        this.detalle = detalle;
        this.fechaValidacion = fechaValidacion;
        this.animalId = animalId;
    }

    /**
     * Retorna el identificador único del registro de validación.
     *
     * @return id de la validación
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del registro de validación.
     *
     * @param id id de la validación
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Indica si el animal fue aprobado por la mayoría de IAs consultadas.
     *
     * @return {@code true} si fue aprobado
     */
    public boolean isAprobado() {
        return aprobado;
    }

    /**
     * Establece si el animal fue aprobado por la mayoría de IAs consultadas.
     *
     * @param aprobado {@code true} si fue aprobado
     */
    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    /**
     * Retorna el número de IAs que aprobaron el registro del animal.
     *
     * @return votos a favor
     */
    public int getVotos() {
        return votos;
    }

    /**
     * Establece el número de IAs que aprobaron el registro del animal.
     *
     * @param votos votos a favor
     */
    public void setVotos(int votos) {
        this.votos = votos;
    }

    /**
     * Retorna el total de IAs consultadas durante la validación.
     *
     * @return total de IAs consultadas
     */
    public int getTotalIAs() {
        return totalIAs;
    }

    /**
     * Establece el total de IAs consultadas durante la validación.
     *
     * @param totalIAs total de IAs consultadas
     */
    public void setTotalIAs(int totalIAs) {
        this.totalIAs = totalIAs;
    }

    /**
     * Retorna el detalle adicional sobre el resultado de la validación.
     *
     * @return detalle del resultado
     */
    public String getDetalle() {
        return detalle;
    }

    /**
     * Establece el detalle adicional sobre el resultado de la validación.
     *
     * @param detalle detalle del resultado
     */
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    /**
     * Retorna la fecha y hora en que se realizó la validación.
     *
     * @return fecha de validación
     */
    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    /**
     * Establece la fecha y hora en que se realizó la validación.
     *
     * @param fechaValidacion fecha de validación
     */
    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    /**
     * Retorna el identificador del animal al que corresponde esta validación.
     *
     * @return id del animal
     */
    public Long getAnimalId() {
        return animalId;
    }

    /**
     * Establece el identificador del animal al que corresponde esta validación.
     *
     * @param animalId id del animal
     */
    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    /**
     * Retorna una representación en cadena del registro de validación con todos sus campos.
     *
     * @return cadena con todos los valores del DTO
     */
    @Override
    public String toString() {
        return "ValidacionIADTO [id=" + id + ", aprobado=" + aprobado + ", votos=" + votos
                + ", totalIAs=" + totalIAs + ", detalle=" + detalle
                + ", fechaValidacion=" + fechaValidacion + ", animalId=" + animalId + "]";
    }

    /**
     * Retorna el código hash basado en todos los campos del DTO.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(animalId, aprobado, detalle, fechaValidacion, id, totalIAs, votos);
    }

    /**
     * Compara este registro de validación con otro objeto por todos sus campos.
     *
     * @param obj objeto a comparar
     * @return {@code true} si todos los campos son iguales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValidacionIADTO other = (ValidacionIADTO) obj;
        return Objects.equals(animalId, other.animalId) && aprobado == other.aprobado
                && Objects.equals(detalle, other.detalle)
                && Objects.equals(fechaValidacion, other.fechaValidacion)
                && Objects.equals(id, other.id) && totalIAs == other.totalIAs && votos == other.votos;
    }
}