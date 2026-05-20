package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ValidacionIADTO {

    private Long id;
    private boolean aprobado;
    private int votos;
    private int totalIAs;
    private String detalle;
    private LocalDateTime fechaValidacion;
    private Long animalId;

    public ValidacionIADTO() {}

    public ValidacionIADTO(boolean aprobado, int votos, int totalIAs,
            String detalle, LocalDateTime fechaValidacion, long animalId) {
        this.aprobado = aprobado;
        this.votos = votos;
        this.totalIAs = totalIAs;
        this.detalle = detalle;
        this.fechaValidacion = fechaValidacion;
        this.animalId = animalId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    public int getVotos() { return votos; }
    public void setVotos(int votos) { this.votos = votos; }

    public int getTotalIAs() { return totalIAs; }
    public void setTotalIAs(int totalIAs) { this.totalIAs = totalIAs; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public LocalDateTime getFechaValidacion() { return fechaValidacion; }
    public void setFechaValidacion(LocalDateTime fechaValidacion) { this.fechaValidacion = fechaValidacion; }

    public long getAnimalId() { return animalId; }
    public void setAnimalId(long animalId) { this.animalId = animalId; }

    @Override
    public String toString() {
        return "ValidacionIADTO [id=" + id + ", aprobado=" + aprobado + ", votos=" + votos
                + ", totalIAs=" + totalIAs + ", detalle=" + detalle
                + ", fechaValidacion=" + fechaValidacion + ", animalId=" + animalId + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(aprobado, animalId, detalle, fechaValidacion, id, totalIAs, votos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ValidacionIADTO other = (ValidacionIADTO) obj;
        return aprobado == other.aprobado
                && animalId == other.animalId
                && Objects.equals(detalle, other.detalle)
                && Objects.equals(fechaValidacion, other.fechaValidacion)
                && Objects.equals(id, other.id)
                && totalIAs == other.totalIAs
                && votos == other.votos;
    }
}