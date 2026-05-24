package co.edu.unbosque.centroadoptivo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.centroadoptivo.dto.UserDTO;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.exception.CiudadException;
import co.edu.unbosque.centroadoptivo.exception.DireccionException;
import co.edu.unbosque.centroadoptivo.exception.EdadException;
import co.edu.unbosque.centroadoptivo.exception.EmailException;
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.PasswordNotValidException;
import co.edu.unbosque.centroadoptivo.exception.TelefonoException;
import co.edu.unbosque.centroadoptivo.exception.UsernameException;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;

/**
 * Servicio para gestionar las operaciones CRUD sobre los usuarios del sistema.
 * Implementa validaciones de datos, encriptación de contraseñas y
 * verificación de unicidad de username y email.
 *
 * <p>Códigos de retorno para {@code create}:
 * <ul>
 *   <li>0 — éxito</li>
 *   <li>1 — username inválido</li>
 *   <li>2 — contraseña inválida</li>
 *   <li>3 — email inválido</li>
 *   <li>4 — nombre completo inválido</li>
 *   <li>5 — teléfono inválido</li>
 *   <li>6 — ciudad inválida</li>
 *   <li>7 — dirección inválida</li>
 *   <li>8 — edad inválida</li>
 *   <li>9 — username ya en uso</li>
 *   <li>10 — email ya en uso</li>
 * </ul>
 *
 * <p>Códigos de retorno para {@code updateById}:
 * <ul>
 *   <li>0 — éxito</li>
 *   <li>1 — username ya en uso por otro usuario</li>
 *   <li>2 — usuario no encontrado</li>
 *   <li>3 — contraseña inválida</li>
 *   <li>4 — email inválido</li>
 *   <li>5 — edad inválida</li>
 *   <li>6 — nombre completo inválido</li>
 *   <li>7 — teléfono inválido</li>
 *   <li>8 — ciudad inválida</li>
 *   <li>9 — dirección inválida</li>
 * </ul>
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Service
public class UserService implements CRUDOperation<UserDTO> {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor vacío requerido por Spring.
     */
    public UserService() {}

    /**
     * Crea un nuevo usuario en el sistema validando todos sus campos.
     * La contraseña se encripta con BCrypt antes de persistir.
     * El rol asignado por defecto es {@code USER}.
     *
     * @param data {@link UserDTO} con los datos del usuario a crear
     * @return 0 si fue exitoso, o un código de error según la validación fallida
     */
    @Override
    public int create(UserDTO data) {
        try {
            LanzadorDeExcepcion.verificarUsername(data.getUsername());
        } catch (UsernameException e) { return 1; }

        try {
            LanzadorDeExcepcion.verificarPassword(data.getPassword());
        } catch (PasswordNotValidException e) { return 2; }

        if (data.getEmail() != null) {
            try {
                LanzadorDeExcepcion.verificarEmail(data.getEmail());
            } catch (EmailException e) { return 3; }
        }
        if (data.getFullName() != null) {
            try {
                LanzadorDeExcepcion.verificarNombre(data.getFullName());
            } catch (NombreException e) { return 4; }
        }
        if (data.getPhone() != null) {
            try {
                LanzadorDeExcepcion.verificarTelefono(data.getPhone());
            } catch (TelefonoException e) { return 5; }
        }
        if (data.getCity() != null) {
            try {
                LanzadorDeExcepcion.verificarCiudad(data.getCity());
            } catch (CiudadException e) { return 6; }
        }
        if (data.getAddress() != null) {
            try {
                LanzadorDeExcepcion.verificarDireccion(data.getAddress());
            } catch (DireccionException e) { return 7; }
        }
        if (data.getAge() > 0) {
            try {
                LanzadorDeExcepcion.verificarEdad(data.getAge());
            } catch (EdadException e) { return 8; }
        }

        if (userRepo.findByUsername(data.getUsername()).isPresent()) return 9;
        if (data.getEmail() != null &&
            userRepo.findByEmail(data.getEmail()).isPresent()) return 10;

        User entity = modelMapper.map(data, User.class);
        entity.setPassword(passwordEncoder.encode(data.getPassword()));
        entity.setRegistrationDate(LocalDateTime.now());
        entity.setRole(User.Role.USER);
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
        userRepo.save(entity);
        return 0;
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * @return lista de {@link UserDTO} con todos los usuarios
     */
    @Override
    public List<UserDTO> getAll() {
        List<User> entityList = userRepo.findAll();
        List<UserDTO> dtoList = new ArrayList<>();
        entityList.forEach(e -> dtoList.add(modelMapper.map(e, UserDTO.class)));
        return dtoList;
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return {@link UserDTO} si el usuario existe, {@code null} si no
     */
    public UserDTO getById(Long id) {
        Optional<User> found = userRepo.findById(id);
        if (found.isPresent()) return modelMapper.map(found.get(), UserDTO.class);
        return null;
    }

    /**
     * Obtiene un usuario por su username.
     *
     * @param username username del usuario a buscar
     * @return {@link UserDTO} si el usuario existe, {@code null} si no
     */
    public UserDTO getByUsername(String username) {
        Optional<User> found = userRepo.findByUsername(username);
        if (found.isPresent()) return modelMapper.map(found.get(), UserDTO.class);
        return null;
    }

    /**
     * Actualiza los datos de un usuario existente.
     * Solo actualiza los campos que no sean nulos en {@code newData}.
     * La contraseña se encripta con BCrypt si se proporciona una nueva.
     * Verifica que el nuevo username no esté en uso por otro usuario.
     *
     * @param id      identificador del usuario a actualizar
     * @param newData {@link UserDTO} con los nuevos datos
     * @return 0 si fue exitoso, o un código de error según la validación fallida
     */
    @Override
    public int updateById(Long id, UserDTO newData) {
        Optional<User> found = userRepo.findById(id);
        if (!found.isPresent()) return 2;

        if (newData.getPassword() != null) {
            try {
                LanzadorDeExcepcion.verificarPassword(newData.getPassword());
            } catch (PasswordNotValidException e) { return 3; }
        }
        if (newData.getEmail() != null) {
            try {
                LanzadorDeExcepcion.verificarEmail(newData.getEmail());
            } catch (EmailException e) { return 4; }
        }
        if (newData.getAge() > 0) {
            try {
                LanzadorDeExcepcion.verificarEdad(newData.getAge());
            } catch (EdadException e) { return 5; }
        }
        if (newData.getFullName() != null) {
            try {
                LanzadorDeExcepcion.verificarNombre(newData.getFullName());
            } catch (NombreException e) { return 6; }
        }
        if (newData.getPhone() != null) {
            try {
                LanzadorDeExcepcion.verificarTelefono(newData.getPhone());
            } catch (TelefonoException e) { return 7; }
        }
        if (newData.getCity() != null) {
            try {
                LanzadorDeExcepcion.verificarCiudad(newData.getCity());
            } catch (CiudadException e) { return 8; }
        }
        if (newData.getAddress() != null) {
            try {
                LanzadorDeExcepcion.verificarDireccion(newData.getAddress());
            } catch (DireccionException e) { return 9; }
        }

        if (newData.getUsername() != null) {
            Optional<User> conflict = userRepo.findByUsername(newData.getUsername());
            if (conflict.isPresent() && !conflict.get().getId().equals(id)) return 1;
        }

        User temp = found.get();
        if (newData.getUsername() != null) temp.setUsername(newData.getUsername());
        if (newData.getPassword() != null)
            temp.setPassword(passwordEncoder.encode(newData.getPassword()));
        if (newData.getEmail() != null) temp.setEmail(newData.getEmail());
        if (newData.getFullName() != null) temp.setFullName(newData.getFullName());
        if (newData.getPhone() != null) temp.setPhone(newData.getPhone());
        if (newData.getCity() != null) temp.setCity(newData.getCity());
        if (newData.getAddress() != null) temp.setAddress(newData.getAddress());
        if (newData.getAge() > 0) temp.setAge(newData.getAge());
        if (newData.getRole() != null) temp.setRole(newData.getRole());
        userRepo.save(temp);
        return 0;
    }

    /**
     * Elimina un usuario por su identificador.
     *
     * @param id identificador del usuario a eliminar
     * @return 0 si fue eliminado exitosamente, 1 si no fue encontrado
     */
    @Override
    public int deleteById(Long id) {
        Optional<User> found = userRepo.findById(id);
        if (found.isPresent()) {
            userRepo.delete(found.get());
            return 0;
        }
        return 1;
    }

    /**
     * Elimina un usuario por su username.
     *
     * @param username username del usuario a eliminar
     * @return 0 si fue eliminado exitosamente, 1 si no fue encontrado
     */
    public int deleteByUsername(String username) {
        Optional<User> found = userRepo.findByUsername(username);
        if (found.isPresent()) {
            userRepo.delete(found.get());
            return 0;
        }
        return 1;
    }

    /**
     * Cuenta el total de usuarios registrados en el sistema.
     *
     * @return número total de usuarios
     */
    @Override
    public long count() { return userRepo.count(); }

    /**
     * Verifica si un usuario existe por su identificador.
     *
     * @param id identificador del usuario
     * @return {@code true} si existe, {@code false} si no
     */
    @Override
    public boolean exist(Long id) { return userRepo.existsById(id); }

    /**
     * Verifica si un username ya está en uso por algún usuario.
     *
     * @param username username a verificar
     * @return {@code true} si ya está en uso, {@code false} si está disponible
     */
    public boolean findUsernameAlreadyTaken(String username) {
        return userRepo.findByUsername(username).isPresent();
    }
}