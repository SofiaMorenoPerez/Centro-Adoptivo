package co.edu.unbosque.centroadoptivo.dto;

/**
 * DTO (Data Transfer Object) para el resultado de validación de un animal por IA.
 * <p>
 * Contiene la información retornada por el servicio de inteligencia artificial
 * tras analizar la imagen de un animal, incluyendo el resultado de aprobación
 * y los datos detectados automáticamente.
 * </p>
 */
public class ResultadoIADTO {

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
    private String detalle;

    /** Constructor por defecto. */
    public ResultadoIADTO() {}

    /**
     * Retorna la especie del animal detectada por la IA.
     *
     * @return especie del animal
     */
    public String getEspecie() {
        return especie;
    }

    /**
     * Establece la especie del animal detectada por la IA.
     *
     * @param especie especie del animal
     */
    public void setEspecie(String especie) {
        this.especie = especie;
    }

    /**
     * Retorna la raza del animal detectada por la IA.
     *
     * @return raza del animal
     */
    public String getRaza() {
        return raza;
    }

    /**
     * Establece la raza del animal detectada por la IA.
     *
     * @param raza raza del animal
     */
    public void setRaza(String raza) {
        this.raza = raza;
    }

    /**
     * Retorna el color del animal detectado por la IA.
     *
     * @return color del animal
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color del animal detectado por la IA.
     *
     * @param color color del animal
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Retorna el rango de edad del animal detectado por la IA.
     *
     * @return edad del animal
     */
    public String getEdad() {
        return edad;
    }

    /**
     * Establece el rango de edad del animal detectado por la IA.
     *
     * @param edad edad del animal
     */
    public void setEdad(String edad) {
        this.edad = edad;
    }

    /**
     * Retorna la clasificación del animal determinada por la IA.
     *
     * @return clasificación del animal
     */
    public String getClasificacion() {
        return clasificacion;
    }

    /**
     * Establece la clasificación del animal determinada por la IA.
     *
     * @param clasificacion clasificación del animal
     */
    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
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
}