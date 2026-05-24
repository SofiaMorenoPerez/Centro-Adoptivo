package co.edu.unbosque.centroadoptivo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa un registro de auditoría del sistema.
 * <p>
 * Almacena información sobre las acciones realizadas por los usuarios,
 * incluyendo quién ejecutó la acción, qué acción fue, cuándo y si fue exitosa.
 * </p>
 */
@Entity
@Table(name = "auditoria")
public class Auditoria {

    /** Identificador único del registro de auditoría, generado automáticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario que ejecutó la acción. */
    private String usuarioEjecutor;

    /** Código de la acción realizada (por ejemplo: CREATE_USER, LOGIN, DELETE_USER). */
    private String accion;

    /** Descripción legible de la acción realizada. */
    @Column(length = 500)
    private String descripcion;

    /** Fecha y hora en que se realizó la acción. */
    private LocalDateTime fecha;

    /** Indica si la acción fue ejecutada exitosamente. */
    private boolean exitoso;

    /** Constructor por defecto. */
    public Auditoria() {}

    /**
     * Constructor que crea un registro de auditoría con la fecha actual.
     *
     * @param usuarioEjecutor nombre de usuario que ejecutó la acción
     * @param accion          código de la acción realizada
     * @param descripcion     descripción legible de la acción
     * @param exitoso         {@code true} si la acción fue exitosa
     */
    public Auditoria(String usuarioEjecutor, String accion, String descripcion, boolean exitoso) {
        this.usuarioEjecutor = usuarioEjecutor;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
        this.exitoso = exitoso;
    }

    /**
     * Retorna el identificador único del registro de auditoría.
     *
     * @return id del registro
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único del registro de auditoría.
     *
     * @param id id del registro
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna el nombre de usuario que ejecutó la acción.
     *
     * @return username del ejecutor
     */
    public String getUsuarioEjecutor() { return usuarioEjecutor; }

    /**
     * Establece el nombre de usuario que ejecutó la acción.
     *
     * @param usuarioEjecutor username del ejecutor
     */
    public void setUsuarioEjecutor(String usuarioEjecutor) { this.usuarioEjecutor = usuarioEjecutor; }

    /**
     * Retorna el código de la acción realizada.
     *
     * @return código de la acción
     */
    public String getAccion() { return accion; }

    /**
     * Establece el código de la acción realizada.
     *
     * @param accion código de la acción
     */
    public void setAccion(String accion) { this.accion = accion; }

    /**
     * Retorna la descripción legible de la acción realizada.
     *
     * @return descripción de la acción
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Establece la descripción legible de la acción realizada.
     *
     * @param descripcion descripción de la acción
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Retorna la fecha y hora en que se realizó la acción.
     *
     * @return fecha de la acción
     */
    public LocalDateTime getFecha() { return fecha; }

    /**
     * Establece la fecha y hora en que se realizó la acción.
     *
     * @param fecha fecha de la acción
     */
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    /**
     * Indica si la acción fue ejecutada exitosamente.
     *
     * @return {@code true} si fue exitosa
     */
    public boolean isExitoso() { return exitoso; }

    /**
     * Establece si la acción fue ejecutada exitosamente.
     *
     * @param exitoso {@code true} si fue exitosa
     */
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
}