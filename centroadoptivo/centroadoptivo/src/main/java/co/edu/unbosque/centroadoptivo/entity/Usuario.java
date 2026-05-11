package co.edu.unbosque.centroadoptivo.entity;

import java.util.Objects;

import co.edu.unbosque.centroadoptivo.enums.RolDeUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="usuario")
public class Usuario {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	
	private String nombre;
	private String apellido;
	private long telefono;
	private String email;
	private String password;
	private String ciudad;
	private String direccion;
	private @Enumerated(EnumType.STRING) RolDeUsuario rol;
	
	public Usuario() {
	}

	public Usuario(String nombre, String apellido, long telefono, String email, String password, String ciudad,
			String direccion, RolDeUsuario rol) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.telefono = telefono;
		this.email = email;
		this.password = password;
		this.ciudad = ciudad;
		this.direccion = direccion;
		this.rol = rol;
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public long getTelefono() {
		return telefono;
	}

	public void setTelefono(long telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public RolDeUsuario getRol() {
		return rol;
	}

	public void setRol(RolDeUsuario rol) {
		this.rol = rol;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", telefono=" + telefono
				+ ", email=" + email + ", password=" + password + ", ciudad=" + ciudad + ", direccion=" + direccion
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(apellido, ciudad, direccion, email, id, nombre, password, telefono);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(apellido, other.apellido) && Objects.equals(ciudad, other.ciudad)
				&& Objects.equals(direccion, other.direccion) && Objects.equals(email, other.email)
				&& Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(password, other.password) && telefono == other.telefono;
	}
	
	
	
	
	
	
	
}
