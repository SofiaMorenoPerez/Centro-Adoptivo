package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import co.edu.unbosque.springfirstapp.model.User.Role;


public class UsuarioDTO {
	
	private Long id;	
	private String username;
	private String password;
	private long nombreCompleto;
	private String email;
	private long telefono;
	private String ciudad;
	private String direccion;
	private LocalDateTime fechaRegistro;
	private Role rol;
	
	public UsuarioDTO() {
		
	}
	
	

	public UsuarioDTO(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}



	public UsuarioDTO(String username, String password, long nombreCompleto, String email, long telefono, String ciudad,
			String direccion, LocalDateTime fechaRegistro, Role rol) {
		super();
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.telefono = telefono;
		this.ciudad = ciudad;
		this.direccion = direccion;
		this.fechaRegistro = fechaRegistro;
		this.rol = rol;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public long getNombreCompleto() {
		return nombreCompleto;
	}



	public void setNombreCompleto(long nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public long getTelefono() {
		return telefono;
	}



	public void setTelefono(long telefono) {
		this.telefono = telefono;
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



	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}



	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}



	public RolDeUsuario getRol() {
		return rol;
	}



	public void setRol(RolDeUsuario rol) {
		this.rol = rol;
	}



	@Override
	public String toString() {
		return "UsuarioDTO [id=" + id + ", username=" + username + ", password=" + password + ", nombreCompleto="
				+ nombreCompleto + ", email=" + email + ", telefono=" + telefono + ", ciudad=" + ciudad + ", direccion="
				+ direccion + ", fechaRegistro=" + fechaRegistro + ", rol=" + rol + "]";
	}



	@Override
	public int hashCode() {
		return Objects.hash(ciudad, direccion, email, fechaRegistro, id, nombreCompleto, password, rol, telefono,
				username);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioDTO other = (UsuarioDTO) obj;
		return Objects.equals(ciudad, other.ciudad) && Objects.equals(direccion, other.direccion)
				&& Objects.equals(email, other.email) && Objects.equals(fechaRegistro, other.fechaRegistro)
				&& Objects.equals(id, other.id) && nombreCompleto == other.nombreCompleto
				&& Objects.equals(password, other.password) && rol == other.rol && telefono == other.telefono
				&& Objects.equals(username, other.username);
	}	
}