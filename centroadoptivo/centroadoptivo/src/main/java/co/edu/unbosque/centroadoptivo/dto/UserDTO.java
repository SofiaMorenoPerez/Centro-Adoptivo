package co.edu.unbosque.centroadoptivo.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.unbosque.centroadoptivo.entity.User.Role;

/**
 * DTO (Data Transfer Object) para la entidad User.
 * <p>
 * Se utiliza para transferir datos de usuarios entre las capas de la aplicación
 * sin exponer directamente la entidad de persistencia.
 * </p>
 */
public class UserDTO {

    /** Identificador único del usuario. */
    private Long id;

    /** Nombre de usuario para autenticación. */
    private String username;

    /** Contraseña del usuario. */
    private String password;

    /** Nombre completo del usuario. */
    private String fullName;

    /** Correo electrónico del usuario. */
    private String email;

    /** Número de teléfono del usuario. */
    private String phone;

    /** Ciudad de residencia del usuario. */
    private String city;

    /** Dirección de residencia del usuario. */
    private String address;

    /** Edad del usuario. */
    private int age;

    /** Rol del usuario en el sistema. */
    private Role role;

    /** Constructor por defecto. */
    public UserDTO() {}

    /**
     * Constructor con username y password.
     *
     * @param username nombre de usuario
     * @param password contraseña del usuario
     */
    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor con username, password y rol.
     *
     * @param username nombre de usuario
     * @param password contraseña del usuario
     * @param role     rol del usuario en el sistema
     */
    public UserDTO(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Retorna el identificador único del usuario.
     *
     * @return id del usuario
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único del usuario.
     *
     * @param id id del usuario
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna el nombre de usuario.
     *
     * @return username del usuario
     */
    public String getUsername() { return username; }

    /**
     * Establece el nombre de usuario.
     *
     * @param username nombre de usuario
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Retorna la contraseña del usuario.
     *
     * @return contraseña del usuario
     */
    public String getPassword() { return password; }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password contraseña del usuario
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Retorna el nombre completo del usuario.
     *
     * @return nombre completo
     */
    public String getFullName() { return fullName; }

    /**
     * Establece el nombre completo del usuario.
     *
     * @param fullName nombre completo
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Retorna el correo electrónico del usuario.
     *
     * @return email del usuario
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email email del usuario
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Retorna el número de teléfono del usuario.
     *
     * @return teléfono del usuario
     */
    public String getPhone() { return phone; }

    /**
     * Establece el número de teléfono del usuario.
     *
     * @param phone teléfono del usuario
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Retorna la ciudad de residencia del usuario.
     *
     * @return ciudad del usuario
     */
    public String getCity() { return city; }

    /**
     * Establece la ciudad de residencia del usuario.
     *
     * @param city ciudad del usuario
     */
    public void setCity(String city) { this.city = city; }

    /**
     * Retorna la dirección de residencia del usuario.
     *
     * @return dirección del usuario
     */
    public String getAddress() { return address; }

    /**
     * Establece la dirección de residencia del usuario.
     *
     * @param address dirección del usuario
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Retorna la edad del usuario.
     *
     * @return edad del usuario
     */
    public int getAge() { return age; }

    /**
     * Establece la edad del usuario.
     *
     * @param age edad del usuario
     */
    public void setAge(int age) { this.age = age; }

    /**
     * Retorna el rol del usuario en el sistema.
     *
     * @return rol del usuario
     */
    public Role getRole() { return role; }

    /**
     * Establece el rol del usuario en el sistema.
     *
     * @param role rol del usuario
     */
    public void setRole(Role role) { this.role = role; }

    /**
     * Retorna el código hash basado en el identificador del usuario.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara este usuario con otro objeto por su identificador.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos tienen el mismo id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        UserDTO other = (UserDTO) obj;

        return Objects.equals(id, other.id);
    }

    /**
     * Retorna una representación en cadena del usuario con sus campos principales.
     *
     * @return cadena con id, username, fullName, email y role
     */
    @Override
    public String toString() {
        return "UserDTO [id=" + id
                + ", username=" + username
                + ", fullName=" + fullName
                + ", email=" + email
                + ", role=" + role
                + "]";
    }
}