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
 * Entidad JPA que representa el resultado de una validación de animal por IA.
 * <p>
 * Almacena la información detectada automáticamente por el servicio de inteligencia
 * artificial al analizar la imagen de un animal, incluyendo especie, raza, color,
 * edad, clasificación y el resultado de aprobación por consenso de múltiples IAs.
 * </p>
 */
@Entity
@Table(name = "resultado_ia")
public class ResultadoIA {

    /** Identificador único del resultado, generado automáticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Especie del animal detectada por la IA. */
    private String especie;

    /** Raza del animal detectada por la IA. */
    private String raza;

    /** Color del animal detectado por la IA. */
    private String color;

    /** Rango de edad del animal detectado por la IA. */
    private String edad;

    /** Clasificación del animal determinada por la IA. */
    private String clasificacion;

    /** Indica si el animal fue aprobado por la mayoría de IAs consultadas. */
    private boolean aprobado;

    /** Número de IAs que aprobaron el registro del animal. */
    private int votos;

    /** Total de IAs consultadas durante la validación. */
    private int totalIAs;

    /** Detalle adicional sobre el resultado de la validación. */
    @Column(length = 1000)
    private String detalle;

    /** Fecha y hora en que se realizó la detección. */
    private LocalDateTime fechaDeteccion;

    /** Animal al que corresponde este resultado de validación. */
    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    /** Constructor por defecto. */
    public ResultadoIA() {}

    /**
     * Constructor con todos los campos de la entidad.
     *
     * @param especie        especie detectada por la IA
     * @param raza           raza detectada por la IA
     * @param color          color detectado por la IA
     * @param edad           rango de edad detectado por la IA
     * @param clasificacion  clasificación determinada por la IA
     * @param aprobado       {@code true} si fue aprobado por la mayoría de IAs
     * @param votos          número de IAs que aprobaron
     * @param totalIAs       total de IAs consultadas
     * @param detalle        detalle adicional del resultado
     * @param fechaDeteccion fecha y hora de la detección
     * @param animal         animal al que corresponde el resultado
     */
    public ResultadoIA(String especie, String raza, String color,
            String edad, String clasificacion, boolean aprobado,
            int votos, int totalIAs, String detalle,
            LocalDateTime fechaDeteccion, Animal animal) {
        this.especie = especie;
        this.raza = raza;
        this.color = color;
        this.edad = edad;
        this.clasificacion = clasificacion;
        this.aprobado = aprobado;
        this.votos = votos;
        this.totalIAs = totalIAs;
        this.detalle = detalle;
        this.fechaDeteccion = fechaDeteccion;
        this.animal = animal;
    }

    /**
     * Retorna el identificador único del resultado.
     *
     * @return id del resultado
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único del resultado.
     *
     * @param id id del resultado
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna la especie del animal detectada por la IA.
     *
     * @return especie del animal
     */
    public String getEspecie() { return especie; }

    /**
     * Establece la especie del animal detectada por la IA.
     *
     * @param especie especie del animal
     */
    public void setEspecie(String especie) { this.especie = especie; }

    /**
     * Retorna la raza del animal detectada por la IA.
     *
     * @return raza del animal
     */
    public String getRaza() { return raza; }

    /**
     * Establece la raza del animal detectada por la IA.
     *
     * @param raza raza del animal
     */
    public void setRaza(String raza) { this.raza = raza; }

    /**
     * Retorna el color del animal detectado por la IA.
     *
     * @return color del animal
     */
    public String getColor() { return color; }

    /**
     * Establece el color del animal detectado por la IA.
     *
     * @param color color del animal
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Retorna el rango de edad del animal detectado por la IA.
     *
     * @return edad del animal
     */
    public String getEdad() { return edad; }

    /**
     * Establece el rango de edad del animal detectado por la IA.
     *
     * @param edad edad del animal
     */
    public void setEdad(String edad) { this.edad = edad; }

    /**
     * Retorna la clasificación del animal determinada por la IA.
     *
     * @return clasificación del animal
     */
    public String getClasificacion() { return clasificacion; }

    /**
     * Establece la clasificación del animal determinada por la IA.
     *
     * @param clasificacion clasificación del animal
     */
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }

    /**
     * Indica si el animal fue aprobado por la mayoría de IAs consultadas.
     *
     * @return {@code true} si fue aprobado
     */
    public boolean isAprobado() { return aprobado; }

    /**
     * Establece si el animal fue aprobado por la mayoría de IAs consultadas.
     *
     * @param aprobado {@code true} si fue aprobado
     */
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    /**
     * Retorna el número de IAs que aprobaron el registro del animal.
     *
     * @return votos a favor
     */
    public int getVotos() { return votos; }

    /**
     * Establece el número de IAs que aprobaron el registro del animal.
     *
     * @param votos votos a favor
     */
    public void setVotos(int votos) { this.votos = votos; }

    /**
     * Retorna el total de IAs consultadas durante la validación.
     *
     * @return total de IAs consultadas
     */
    public int getTotalIAs() { return totalIAs; }

    /**
     * Establece el total de IAs consultadas durante la validación.
     *
     * @param totalIAs total de IAs consultadas
     */
    public void setTotalIAs(int totalIAs) { this.totalIAs = totalIAs; }

    /**
     * Retorna el detalle adicional sobre el resultado de la validación.
     *
     * @return detalle del resultado
     */
    public String getDetalle() { return detalle; }

    /**
     * Establece el detalle adicional sobre el resultado de la validación.
     *
     * @param detalle detalle del resultado
     */
    public void setDetalle(String detalle) { this.detalle = detalle; }

    /**
     * Retorna la fecha y hora en que se realizó la detección.
     *
     * @return fecha de detección
     */
    public LocalDateTime getFechaDeteccion() { return fechaDeteccion; }

    /**
     * Establece la fecha y hora en que se realizó la detección.
     *
     * @param fechaDeteccion fecha de detección
     */
    public void setFechaDeteccion(LocalDateTime fechaDeteccion) { this.fechaDeteccion = fechaDeteccion; }

    /**
     * Retorna el animal al que corresponde este resultado de validación.
     *
     * @return animal validado
     */
    public Animal getAnimal() { return animal; }

    /**
     * Establece el animal al que corresponde este resultado de validación.
     *
     * @param animal animal validado
     */
    public void setAnimal(Animal animal) { this.animal = animal; }

    /**
     * Retorna una representación en cadena del resultado con sus campos principales.
     *
     * @return cadena con id, especie, raza, aprobado y votos
     */
    @Override
    public String toString() {
        return "ResultadoIA [id=" + id + ", especie=" + especie
                + ", raza=" + raza + ", aprobado=" + aprobado
                + ", votos=" + votos + "]";
    }

    /**
     * Retorna el código hash basado en el identificador del resultado.
     *
     * @return código hash
     */
    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Compara este resultado con otro objeto por su identificador.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos tienen el mismo id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ResultadoIA other = (ResultadoIA) obj;
        return Objects.equals(id, other.id);
    }
}