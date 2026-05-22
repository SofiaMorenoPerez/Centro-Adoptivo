package co.edu.unbosque.centroadoptivo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensaje;

    private boolean leida;

    private LocalDateTime creadaEn;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private User destinatario;

    public Notificacion() {}

    public Notificacion(String mensaje, User destinatario) {
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.leida = false;
        this.creadaEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    public User getDestinatario() { return destinatario; }
    public void setDestinatario(User destinatario) { this.destinatario = destinatario; }

    @Override
    public String toString() {
        return "Notificacion [id=" + id + ", mensaje=" + mensaje
                + ", leida=" + leida + ", creadaEn=" + creadaEn + "]";
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Notificacion other = (Notificacion) obj;
        return Objects.equals(id, other.id);
    }
}