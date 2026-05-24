package co.edu.unbosque.centroadoptivo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa un usuario registrado en el sistema del centro adoptivo.
 * <p>
 * Implementa {@link UserDetails} de Spring Security para gestionar la autenticación
 * y autorización. Cada usuario puede publicar animales en adopción y/o adoptar animales.
 * </p>
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 */
@Entity
@Table(name = "app_user")
public class User implements UserDetails {

    /** Identificador de versión para la serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador único autogenerado del usuario. */
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    /** Nombre de usuario único utilizado para autenticación. */
    @Column(unique = true)
    private String username;

    /** Contraseña del usuario (almacenada de forma codificada). */
    private String password;

    /** Nombre completo del usuario. */
    private String fullName;

    /** Correo electrónico único del usuario. */
    @Column(unique = true)
    private String email;

    /** Número de teléfono de contacto del usuario. */
    private String phone;

    /** Ciudad de residencia del usuario. */
    private String city;

    /** Dirección de residencia del usuario. */
    private String address;

    /** Edad del usuario. */
    private int age;

    /** Fecha y hora en que el usuario se registró en el sistema. */
    private LocalDateTime registrationDate;

    /** Rol del usuario dentro del sistema (USER o ADMIN). */
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Indica si la cuenta del usuario no ha expirado. */
    private boolean accountNonExpired;

    /** Indica si la cuenta del usuario no está bloqueada. */
    private boolean accountNonLocked;

    /** Indica si las credenciales del usuario no han expirado. */
    private boolean credentialsNonExpired;

    /** Indica si la cuenta del usuario está habilitada. */
    private boolean enabled;

    /**
     * Lista de animales publicados por este usuario como posibles candidatos a adopción.
     * Se excluye de la serialización JSON para evitar referencias circulares.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "publisher", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Animal> publishedAnimals = new ArrayList<>();

    /**
     * Lista de animales adoptados por este usuario.
     * Se excluye de la serialización JSON para evitar referencias circulares.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "adopter", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Animal> adoptedAnimals = new ArrayList<>();

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la cuenta con todos los flags de seguridad en {@code true}
     * y asigna el rol {@link Role#USER} por defecto.
     * </p>
     */
    public User() {
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.role = Role.USER;
    }

    /**
     * Constructor con credenciales básicas.
     *
     * @param username nombre de usuario único
     * @param password contraseña del usuario
     */
    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor con credenciales y rol.
     * <p>
     * Además asigna automáticamente la fecha de registro al momento de creación.
     * </p>
     *
     * @param username nombre de usuario único
     * @param password contraseña del usuario
     * @param role     rol asignado al usuario
     */
    public User(String username, String password, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        this.registrationDate = LocalDateTime.now();
    }

    /**
     * Constructor con todos los datos personales del usuario, sin especificar rol.
     *
     * @param username         nombre de usuario único
     * @param password         contraseña del usuario
     * @param fullName         nombre completo del usuario
     * @param email            correo electrónico único
     * @param phone            número de teléfono
     * @param city             ciudad de residencia
     * @param address          dirección de residencia
     * @param age              edad del usuario
     * @param registrationDate fecha y hora de registro
     */
    public User(String username, String password, String fullName,
            String email, String phone, String city, String address,
            int age, LocalDateTime registrationDate) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.age = age;
        this.registrationDate = registrationDate;
    }

    /**
     * Constructor completo con todos los datos personales y rol del usuario.
     *
     * @param username         nombre de usuario único
     * @param password         contraseña del usuario
     * @param fullName         nombre completo del usuario
     * @param email            correo electrónico único
     * @param phone            número de teléfono
     * @param city             ciudad de residencia
     * @param address          dirección de residencia
     * @param age              edad del usuario
     * @param registrationDate fecha y hora de registro
     * @param role             rol asignado al usuario
     */
    public User(String username, String password, String fullName,
            String email, String phone, String city, String address,
            int age, LocalDateTime registrationDate, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.age = age;
        this.registrationDate = registrationDate;
        this.role = role;
    }

    /**
     * Roles disponibles para los usuarios del sistema.
     */
    public enum Role {
        /** Usuario regular con permisos básicos. */
        USER,
        /** Administrador con permisos completos. */
        ADMIN
    }

    /**
     * Retorna las autoridades (roles) concedidas al usuario para Spring Security.
     * <p>
     * El rol se expone con el prefijo {@code ROLE_} según la convención de Spring Security.
     * </p>
     *
     * @return colección con la autoridad correspondiente al rol del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Indica si la cuenta del usuario no ha expirado.
     *
     * @return {@code true} si la cuenta está vigente
     */
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }

    /**
     * Indica si la cuenta del usuario no está bloqueada.
     *
     * @return {@code true} si la cuenta no está bloqueada
     */
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }

    /**
     * Indica si las credenciales del usuario no han expirado.
     *
     * @return {@code true} si las credenciales están vigentes
     */
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }

    /**
     * Indica si la cuenta del usuario está habilitada.
     *
     * @return {@code true} si la cuenta está activa
     */
    @Override public boolean isEnabled() { return enabled; }

    /**
     * Retorna el identificador único del usuario.
     *
     * @return id del usuario
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador único del usuario.
     *
     * @param id nuevo id del usuario
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna el nombre de usuario utilizado para autenticación.
     *
     * @return nombre de usuario
     */
    public String getUsername() { return username; }

    /**
     * Establece el nombre de usuario.
     *
     * @param username nuevo nombre de usuario
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Retorna la contraseña codificada del usuario.
     *
     * @return contraseña del usuario
     */
    public String getPassword() { return password; }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password nueva contraseña (debe enviarse ya codificada)
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
     * @param fullName nuevo nombre completo
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Retorna el correo electrónico del usuario.
     *
     * @return correo electrónico
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email nuevo correo electrónico
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Retorna el número de teléfono del usuario.
     *
     * @return número de teléfono
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Establece el número de teléfono del usuario.
     *
     * @param phone nuevo número de teléfono
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retorna la ciudad de residencia del usuario.
     *
     * @return ciudad
     */
    public String getCity() { return city; }

    /**
     * Establece la ciudad de residencia del usuario.
     *
     * @param city nueva ciudad
     */
    public void setCity(String city) { this.city = city; }

    /**
     * Retorna la dirección de residencia del usuario.
     *
     * @return dirección
     */
    public String getAddress() { return address; }

    /**
     * Establece la dirección de residencia del usuario.
     *
     * @param address nueva dirección
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Retorna la edad del usuario.
     *
     * @return edad
     */
    public int getAge() { return age; }

    /**
     * Establece la edad del usuario.
     *
     * @param age nueva edad
     */
    public void setAge(int age) { this.age = age; }

    /**
     * Retorna la fecha y hora de registro del usuario.
     *
     * @return fecha de registro
     */
    public LocalDateTime getRegistrationDate() { return registrationDate; }

    /**
     * Establece la fecha y hora de registro del usuario.
     *
     * @param registrationDate nueva fecha de registro
     */
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    /**
     * Retorna el rol del usuario en el sistema.
     *
     * @return rol del usuario
     */
    public Role getRole() { return role; }

    /**
     * Establece el rol del usuario en el sistema.
     *
     * @param role nuevo rol
     */
    public void setRole(Role role) { this.role = role; }

    /**
     * Establece si la cuenta del usuario ha expirado o no.
     *
     * @param accountNonExpired {@code true} si la cuenta sigue vigente
     */
    public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }

    /**
     * Establece si la cuenta del usuario está bloqueada o no.
     *
     * @param accountNonLocked {@code true} si la cuenta no está bloqueada
     */
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    /**
     * Establece si las credenciales del usuario han expirado o no.
     *
     * @param credentialsNonExpired {@code true} si las credenciales siguen vigentes
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }

    /**
     * Establece si la cuenta del usuario está habilitada.
     *
     * @param enabled {@code true} para habilitar la cuenta
     */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    /**
     * Retorna la lista de animales publicados por este usuario.
     *
     * @return lista de animales publicados
     */
    public List<Animal> getPublishedAnimals() { return publishedAnimals; }

    /**
     * Establece la lista de animales publicados por este usuario.
     *
     * @param publishedAnimals nueva lista de animales publicados
     */
    public void setPublishedAnimals(List<Animal> publishedAnimals) { this.publishedAnimals = publishedAnimals; }

    /**
     * Retorna la lista de animales adoptados por este usuario.
     *
     * @return lista de animales adoptados
     */
    public List<Animal> getAdoptedAnimals() { return adoptedAnimals; }

    /**
     * Establece la lista de animales adoptados por este usuario.
     *
     * @param adoptedAnimals nueva lista de animales adoptados
     */
    public void setAdoptedAnimals(List<Animal> adoptedAnimals) { this.adoptedAnimals = adoptedAnimals; }

    /**
     * Retorna una representación en cadena del usuario con sus datos principales.
     *
     * @return cadena con id, username, fullName, email y role
     */
    @Override
    public String toString() {
        return "User [id=" + id
                + ", username=" + username
                + ", fullName=" + fullName
                + ", email=" + email
                + ", role=" + role
                + "]";
    }

    /**
     * Calcula el hash del usuario basado únicamente en su {@code id}.
     *
     * @return valor hash del usuario
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara este usuario con otro objeto para determinar igualdad.
     * <p>
     * Dos usuarios son iguales si y solo si tienen el mismo {@code id}.
     * </p>
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos usuarios tienen el mismo {@code id}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        User other = (User) obj;

        return Objects.equals(id, other.id);
    }
}