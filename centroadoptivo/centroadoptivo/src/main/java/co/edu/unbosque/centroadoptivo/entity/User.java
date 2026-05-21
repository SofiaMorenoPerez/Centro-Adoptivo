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

@Entity
@Table(name = "app_user")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String fullName;

    @Column(unique = true)
    private String email;

    private String phone;
    private String city;
    private String address;
    private int age;
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @JsonIgnore
    @OneToMany(mappedBy = "publisher", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Animal> publishedAnimals = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "adopter", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Animal> adoptedAnimals = new ArrayList<>();

    public User() {
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.role = Role.USER;
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        this.registrationDate = LocalDateTime.now();
    }

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

    public enum Role {
        /** Regular user with basic permissions */
        USER,
        /** Administrator with full permissions */
        ADMIN
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    @Override public boolean isEnabled() { return enabled; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    
    public String getPhone() {
		return phone;
	}
	
    public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
    public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<Animal> getPublishedAnimals() { return publishedAnimals; }
    public void setPublishedAnimals(List<Animal> publishedAnimals) { this.publishedAnimals = publishedAnimals; }

    public List<Animal> getAdoptedAnimals() { return adoptedAnimals; }
    public void setAdoptedAnimals(List<Animal> adoptedAnimals) { this.adoptedAnimals = adoptedAnimals; }

    

    @Override
    public String toString() {
        return "User [id=" + id
                + ", username=" + username
                + ", fullName=" + fullName
                + ", email=" + email
                + ", role=" + role
                + "]";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
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