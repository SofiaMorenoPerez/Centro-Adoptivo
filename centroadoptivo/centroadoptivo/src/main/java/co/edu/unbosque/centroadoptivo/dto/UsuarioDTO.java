package co.edu.unbosque.centroadoptivo.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import co.edu.unbosque.centroadoptivo.entity.Usuario.Rol;

public class UsuarioDTO {

    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    private long telefono;
    private String ciudad;
    private String direccion;
    private LocalDateTime fechaRegistro;
    private Rol rol;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String username, String password, String nombreCompleto,
            String email, long telefono, String ciudad, String direccion,
            LocalDateTime fechaRegistro) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    public UsuarioDTO(String username, String password, String nombreCompleto,
            String email, long telefono, String ciudad, String direccion,
            LocalDateTime fechaRegistro, Rol rol) {
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getTelefono() { return telefono; }
    public void setTelefono(long telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "UsuarioDTO [id=" + id + ", username=" + username + ", nombreCompleto=" + nombreCompleto
                + ", email=" + email + ", telefono=" + telefono + ", ciudad=" + ciudad
                + ", direccion=" + direccion + ", fechaRegistro=" + fechaRegistro + ", rol=" + rol + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UsuarioDTO other = (UsuarioDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(username, other.username)
                && Objects.equals(email, other.email);
    }
}