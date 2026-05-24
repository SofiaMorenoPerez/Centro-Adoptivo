package co.edu.unbosque.centroadoptivo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa una notificación del sistema.
 * <p>
 * Almacena mensajes dirigidos a un usuario específico, con información
 * sobre su estado de lectura y la fecha en que fue creada.
 * </p>
 */
@Entity
@Table(name = "notificacion")
public class Notificacion {

    /** Identificador único de la notificación, generado automáticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Contenido del mensaje de la notificación. */
    private String mensaje;

    /** Indica si la notificación ha sido leída por el destinatario. */
    private boolean leida;

    /** Fecha y hora en que fue creada la notificación. */
    private LocalDateTime creadaEn;

    /** Usuario destinatario de la notificación. */
    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private User destinatario;

    /** Constructor por defecto. */
    public Notificacion() {}

    /**
     * Constructor que crea una notificación no leída con la fecha actual.
     *
     * @param mensaje      contenido del mensaje
     * @param destinatario usuario destinatario de la notificación
     */
    public Notificacion(String mensaje, User destinatario) {
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.leida = false;
        this.creadaEn = LocalDateTime.now();
    }

    /**
     * Retorna el identificador único de la notificación.
     *
     * @return id de la notificación
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único de la notificación.
     *
     * @param id id de la notificación
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna el contenido del mensaje de la notificación.
     *
     * @return mensaje de la notificación
     */
    public String getMensaje() { return mensaje; }

    /**
     * Establece el contenido del mensaje de la notificación.
     *
     * @param mensaje mensaje de la notificación
     */
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    /**
     * Indica si la notificación ha sido leída.
     *
     * @return {@code true} si fue leída
     */
    public boolean isLeida() { return leida; }

    /**
     * Establece el estado de lectura de la notificación.
     *
     * @param leida {@code true} si fue leída
     */
    public void setLeida(boolean leida) { this.leida = leida; }

    /**
     * Retorna la fecha y hora de creación de la notificación.
     *
     * @return fecha de creación
     */
    public LocalDateTime getCreadaEn() { return creadaEn; }

    /**
     * Establece la fecha y hora de creación de la notificación.
     *
     * @param creadaEn fecha de creación
     */
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    /**
     * Retorna el usuario destinatario de la notificación.
     *
     * @return usuario destinatario
     */
    public User getDestinatario() { return destinatario; }

    /**
     * Establece el usuario destinatario de la notificación.
     *
     * @param destinatario usuario destinatario
     */
    public void setDestinatario(User destinatario) { this.destinatario = destinatario; }

    /**
     * Retorna una representación en cadena de la notificación con sus campos principales.
     *
     * @return cadena con id, mensaje, leida y creadaEn
     */
    @Override
    public String toString() {
        return "Notificacion [id=" + id + ", mensaje=" + mensaje
                + ", leida=" + leida + ", creadaEn=" + creadaEn + "]";
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
        Notificacion other = (Notificacion) obj;
        return Objects.equals(id, other.id);
    }
}