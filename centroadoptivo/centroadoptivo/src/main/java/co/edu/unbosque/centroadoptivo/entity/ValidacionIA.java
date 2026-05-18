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

@Entity
@Table(name = "validacion_ia")
public class ValidacionIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean aprobado;

    private int votos;

    private int totalIAs;

    @Column(length = 1000)
    private String detalle;

    private LocalDateTime fechaValidacion;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public ValidacionIA() {}

    public ValidacionIA(boolean aprobado, int votos, int totalIAs,
            String detalle, LocalDateTime fechaValidacion, Animal animal) {
        this.aprobado = aprobado;
        this.votos = votos;
        this.totalIAs = totalIAs;
        this.detalle = detalle;
        this.fechaValidacion = fechaValidacion;
        this.animal = animal;
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
    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    @Override
    public String toString() {
        return "ValidacionIA [id=" + id + ", aprobado=" + aprobado
                + ", votos=" + votos + ", totalIAs=" + totalIAs
                + ", detalle=" + detalle + ", fechaValidacion=" + fechaValidacion + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(aprobado, detalle, fechaValidacion, id, totalIAs, votos);
    }

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