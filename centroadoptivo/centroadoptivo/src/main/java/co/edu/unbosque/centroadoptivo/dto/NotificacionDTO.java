package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) para la entidad Notificacion.
 * <p>
 * Se utiliza para transferir datos de notificaciones entre las capas
 * de la aplicación sin exponer directamente la entidad de persistencia.
 * </p>
 */
public class NotificacionDTO {

    /** Identificador único de la notificación. */
    private Long id;

    /** Contenido del mensaje de la notificación. */
    private String mensaje;

    /** Indica si la notificación ha sido leída por el destinatario. */
    private boolean leida;

    /** Fecha y hora en que fue creada la notificación. */
    private LocalDateTime creadaEn;

    /** Identificador del usuario destinatario de la notificación. */
    private Long destinatarioId;

    /** Constructor por defecto. */
    public NotificacionDTO() {}

    /**
     * Constructor que crea una notificación no leída con la fecha actual.
     *
     * @param mensaje        contenido del mensaje
     * @param destinatarioId identificador del usuario destinatario
     */
    public NotificacionDTO(String mensaje, Long destinatarioId) {
        this.mensaje = mensaje;
        this.destinatarioId = destinatarioId;
        this.leida = false;
        this.creadaEn = LocalDateTime.now();
    }

    /**
     * Retorna el identificador único de la notificación.
     *
     * @return id de la notificación
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la notificación.
     *
     * @param id id de la notificación
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el contenido del mensaje de la notificación.
     *
     * @return mensaje de la notificación
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el contenido del mensaje de la notificación.
     *
     * @param mensaje mensaje de la notificación
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Indica si la notificación ha sido leída.
     *
     * @return {@code true} si fue leída
     */
    public boolean isLeida() {
        return leida;
    }

    /**
     * Establece el estado de lectura de la notificación.
     *
     * @param leida {@code true} si fue leída
     */
    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    /**
     * Retorna la fecha y hora de creación de la notificación.
     *
     * @return fecha de creación
     */
    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    /**
     * Establece la fecha y hora de creación de la notificación.
     *
     * @param creadaEn fecha de creación
     */
    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }

    /**
     * Retorna el identificador del usuario destinatario de la notificación.
     *
     * @return id del destinatario
     */
    public Long getDestinatarioId() {
        return destinatarioId;
    }

    /**
     * Establece el identificador del usuario destinatario de la notificación.
     *
     * @param destinatarioId id del destinatario
     */
    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    /**
     * Retorna una representación en cadena de la notificación.
     *
     * @return cadena con los valores de todos los campos
     */
    @Override
    public String toString() {
        return "NotificacionDTO [id=" + id + ", mensaje=" + mensaje
                + ", leida=" + leida + ", creadaEn=" + creadaEn
                + ", destinatarioId=" + destinatarioId + "]";
    }

    /**
     * Retorna el código hash basado en el identificador de la notificación.
     *
     * @return código hash
     */
    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Compara esta notificación con otro objeto por su identificador.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos tienen el mismo id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NotificacionDTO other = (NotificacionDTO) obj;
        return Objects.equals(id, other.id);
    }
}