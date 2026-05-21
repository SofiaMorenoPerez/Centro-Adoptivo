package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalClasificacion;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalEdad;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalEstado;

public class AnimalDTO {

    private Long id;
    private String nombre;
    private AnimalEdad edad;
    private boolean esterilizado;
    private boolean vacunado;
    private String especie;
    private String raza;
    private String color;
    private String observaciones;
    private String imagen;
    private LocalDateTime publicadoEn;
    private LocalDateTime actualizadoEn;
    private AnimalClasificacion clasificacion;
    private AnimalEstado estado;

    private Long publicadorId;
    private Long adoptanteId;

    public AnimalDTO() {}

    
    public AnimalDTO(String nombre, AnimalEdad edad, boolean esterilizado, boolean vacunado, String especie,
			String raza, String color, String observaciones, String imagen, LocalDateTime publicadoEn,
			LocalDateTime actualizadoEn, AnimalClasificacion clasificacion, AnimalEstado estado, Long publicadorId,
			Long adoptanteId) {
    	
		super();
		this.nombre = nombre;
		this.edad = edad;
		this.esterilizado = esterilizado;
		this.vacunado = vacunado;
		this.especie = especie;
		this.raza = raza;
		this.color = color;
		this.observaciones = observaciones;
		this.imagen = imagen;
		this.publicadoEn = publicadoEn;
		this.actualizadoEn = actualizadoEn;
		this.clasificacion = clasificacion;
		this.estado = estado;
		this.publicadorId = publicadorId;
		this.adoptanteId = adoptanteId;
	}
    
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public AnimalEdad getEdad() {
		return edad;
	}


	public void setEdad(AnimalEdad edad) {
		this.edad = edad;
	}


	public boolean isEsterilizado() {
		return esterilizado;
	}


	public void setEsterilizado(boolean esterilizado) {
		this.esterilizado = esterilizado;
	}


	public boolean isVacunado() {
		return vacunado;
	}


	public void setVacunado(boolean vacunado) {
		this.vacunado = vacunado;
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


	public String getObservaciones() {
		return observaciones;
	}


	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}


	public String getImagen() {
		return imagen;
	}


	public void setImagen(String imagen) {
		this.imagen = imagen;
	}


	public LocalDateTime getPublicadoEn() {
		return publicadoEn;
	}


	public void setPublicadoEn(LocalDateTime publicadoEn) {
		this.publicadoEn = publicadoEn;
	}


	public LocalDateTime getActualizadoEn() {
		return actualizadoEn;
	}


	public void setActualizadoEn(LocalDateTime actualizadoEn) {
		this.actualizadoEn = actualizadoEn;
	}


	public AnimalClasificacion getClasificacion() {
		return clasificacion;
	}


	public void setClasificacion(AnimalClasificacion clasificacion) {
		this.clasificacion = clasificacion;
	}


	public AnimalEstado getEstado() {
		return estado;
	}


	public void setEstado(AnimalEstado estado) {
		this.estado = estado;
	}


	public Long getPublicadorId() {
		return publicadorId;
	}


	public void setPublicadorId(Long publicadorId) {
		this.publicadorId = publicadorId;
	}


	public Long getAdoptanteId() {
		return adoptanteId;
	}


	public void setAdoptanteId(Long adoptanteId) {
		this.adoptanteId = adoptanteId;
	}


	@Override
    public int hashCode() {
        return Objects.hash(actualizadoEn, clasificacion, color, edad, especie,
                estado, esterilizado, id, imagen, nombre, observaciones,
                publicadoEn, raza, vacunado, publicadorId, adoptanteId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AnimalDTO other = (AnimalDTO) obj;
        return Objects.equals(actualizadoEn, other.actualizadoEn)
                && clasificacion == other.clasificacion
                && Objects.equals(color, other.color)
                && edad == other.edad
                && Objects.equals(especie, other.especie)
                && estado == other.estado
                && esterilizado == other.esterilizado
                && Objects.equals(id, other.id)        // ✅ Objects.equals para Long
                && Objects.equals(imagen, other.imagen)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(observaciones, other.observaciones)
                && Objects.equals(publicadoEn, other.publicadoEn)
                && Objects.equals(raza, other.raza)
                && vacunado == other.vacunado
                && Objects.equals(publicadorId, other.publicadorId)
                && Objects.equals(adoptanteId, other.adoptanteId);
    }

    @Override
    public String toString() {
        return "AnimalDTO [id=" + id + ", nombre=" + nombre + ", especie=" + especie
                + ", raza=" + raza + ", estado=" + estado + ", publicadorId=" + publicadorId
                + ", adoptanteId=" + adoptanteId + "]";
    }
}