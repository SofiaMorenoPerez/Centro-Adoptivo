package co.edu.unbosque.centroadoptivo.dto;

import java.util.Objects;
import co.edu.unbosque.centroadoptivo.entity.User.Role;

public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String address;
    private int age;
    private Role role;

    public UserDTO() {}

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId()                            { return id; }
    public void setId(Long id)                    { this.id = id; }
    public String getUsername()                    { return username; }
    public void setUsername(String username)      { this.username = username; }
    public String getPassword()                    { return password; }
    public void setPassword(String password)      { this.password = password; }
    public String getFullName()                    { return fullName; }
    public void setFullName(String fullName)      { this.fullName = fullName; }
    public String getEmail()                       { return email; }
    public void setEmail(String email)            { this.email = email; }
    public String getPhone()                       { return phone; }
    public void setPhone(String phone)            { this.phone = phone; }
    public String getCity()                        { return city; }
    public void setCity(String city)              { this.city = city; }
    public String getAddress()                     { return address; }
    public void setAddress(String address)        { this.address = address; }
    public int getAge()                            { return age; }
    public void setAge(int age)                   { this.age = age; }
    public Role getRole()                          { return role; }
    public void setRole(Role role)                { this.role = role; }

	@Override
	public int hashCode() {
		return Objects.hash(address, age, city, email, fullName, id, password, phone, role, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return Objects.equals(address, other.address) && age == other.age && Objects.equals(city, other.city)
				&& Objects.equals(email, other.email) && Objects.equals(fullName, other.fullName)
				&& Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(phone, other.phone) && role == other.role && Objects.equals(username, other.username);
	}

    
}