package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class NotificacionDTO {

    private Long id;
    private String mensaje;
    private boolean leida;
    private LocalDateTime creadaEn;
    private Long destinatarioId;

    public NotificacionDTO() {}

    public NotificacionDTO(String mensaje, Long destinatarioId) {
        this.mensaje = mensaje;
        this.destinatarioId = destinatarioId;
        this.leida = false;
        this.creadaEn = LocalDateTime.now();
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public boolean isLeida() {
		return leida;
	}

	public void setLeida(boolean leida) {
		this.leida = leida;
	}

	public LocalDateTime getCreadaEn() {
		return creadaEn;
	}

	public void setCreadaEn(LocalDateTime creadaEn) {
		this.creadaEn = creadaEn;
	}

	public Long getDestinatarioId() {
		return destinatarioId;
	}

	public void setDestinatarioId(Long destinatarioId) {
		this.destinatarioId = destinatarioId;
	}

	@Override
    public String toString() {
        return "NotificacionDTO [id=" + id + ", mensaje=" + mensaje
                + ", leida=" + leida + ", creadaEn=" + creadaEn
                + ", destinatarioId=" + destinatarioId + "]";
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NotificacionDTO other = (NotificacionDTO) obj;
        return Objects.equals(id, other.id);
    }
}