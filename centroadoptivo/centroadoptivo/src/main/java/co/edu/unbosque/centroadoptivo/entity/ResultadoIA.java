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
@Table(name = "resultado_ia")
public class ResultadoIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String especie;
    private String raza;
    private String color;
    private String edad;
    private String clasificacion;
    private boolean aprobado;
    private int votos;
    private int totalIAs;

    @Column(length = 1000)
    private String detalle;

    private LocalDateTime fechaDeteccion;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public ResultadoIA() {}

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

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		this.raza = raza;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getEdad() {
		return edad;
	}

	public void setEdad(String edad) {
		this.edad = edad;
	}

	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public boolean isAprobado() {
		return aprobado;
	}

	public void setAprobado(boolean aprobado) {
		this.aprobado = aprobado;
	}

	public int getVotos() {
		return votos;
	}

	public void setVotos(int votos) {
		this.votos = votos;
	}

	public int getTotalIAs() {
		return totalIAs;
	}

	public void setTotalIAs(int totalIAs) {
		this.totalIAs = totalIAs;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public LocalDateTime getFechaDeteccion() {
		return fechaDeteccion;
	}

	public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
		this.fechaDeteccion = fechaDeteccion;
	}

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	@Override
    public String toString() {
        return "ResultadoIA [id=" + id + ", especie=" + especie
                + ", raza=" + raza + ", aprobado=" + aprobado
                + ", votos=" + votos + "]";
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ResultadoIA other = (ResultadoIA) obj;
        return Objects.equals(id, other.id);
    }
}