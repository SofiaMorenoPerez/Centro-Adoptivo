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
import co.edu.unbosque.centroadoptivo.util.AESUtil;

@Service
public class UserService implements CRUDOperation<UserDTO> {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService() {}

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Encripta los campos sensibles de la entidad antes de persistir */
    private void encryptUser(User entity) {
        if (entity.getUsername() != null)
            entity.setUsername(AESUtil.encrypt(entity.getUsername()));
        if (entity.getEmail() != null)
            entity.setEmail(AESUtil.encrypt(entity.getEmail()));
        if (entity.getFullName() != null)
            entity.setFullName(AESUtil.encrypt(entity.getFullName()));
        if (entity.getPhone() != null)
            entity.setPhone(AESUtil.encrypt(entity.getPhone()));
        if (entity.getCity() != null)
            entity.setCity(AESUtil.encrypt(entity.getCity()));
        if (entity.getAddress() != null)
            entity.setAddress(AESUtil.encrypt(entity.getAddress()));
    }

    /** Desencripta los campos sensibles de la entidad al leer */
    private void decryptUser(User entity) {
        if (entity.getUsername() != null)
            entity.setUsername(AESUtil.decrypt(entity.getUsername()));
        if (entity.getEmail() != null)
            entity.setEmail(AESUtil.decrypt(entity.getEmail()));
        if (entity.getFullName() != null)
            entity.setFullName(AESUtil.decrypt(entity.getFullName()));
        if (entity.getPhone() != null)
            entity.setPhone(AESUtil.decrypt(entity.getPhone()));
        if (entity.getCity() != null)
            entity.setCity(AESUtil.decrypt(entity.getCity()));
        if (entity.getAddress() != null)
            entity.setAddress(AESUtil.decrypt(entity.getAddress()));
    }

    /** Convierte entidad desencriptada a DTO */
    private UserDTO toDTO(User entity) {
        decryptUser(entity);
        return modelMapper.map(entity, UserDTO.class);
    }

    
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

        
        if (userRepo.findByUsername(AESUtil.encrypt(data.getUsername())).isPresent()) return 9;
        if (data.getEmail() != null &&
            userRepo.findByEmail(AESUtil.encrypt(data.getEmail())).isPresent()) return 10;

        
        User entity = modelMapper.map(data, User.class);
        entity.setPassword(passwordEncoder.encode(data.getPassword()));
        entity.setRegistrationDate(LocalDateTime.now());
        if (entity.getRole() == null) entity.setRole(User.Role.USER); // ← fix principal
        if (data.getRole() != null) entity.setRole(data.getRole());
        entity.setAccountNonExpired(true);      // ← evita que ModelMapper los deje en false
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
        encryptUser(entity);
        userRepo.save(entity);
        return 0;
    }

    

    @Override
    public List<UserDTO> getAll() {
        List<User> entityList = userRepo.findAll();
        List<UserDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(toDTO(entity)));
        return dtoList;
    }

    public UserDTO getById(Long id) {
        Optional<User> found = userRepo.findById(id);
        if (found.isPresent()) return toDTO(found.get());
        return null;
    }

    
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
            Optional<User> newFound = userRepo.findByUsername(
                AESUtil.encrypt(newData.getUsername()));
            if (newFound.isPresent() && !newFound.get().getId().equals(id)) return 1;
        }

        
        User temp = found.get();
        if (newData.getUsername() != null)
            temp.setUsername(AESUtil.encrypt(newData.getUsername()));
        if (newData.getPassword() != null)
            temp.setPassword(passwordEncoder.encode(newData.getPassword()));
        if (newData.getEmail() != null)
            temp.setEmail(AESUtil.encrypt(newData.getEmail()));
        if (newData.getFullName() != null)
            temp.setFullName(AESUtil.encrypt(newData.getFullName()));
        if (newData.getPhone() != null)
            temp.setPhone(AESUtil.encrypt(newData.getPhone()));
        if (newData.getCity() != null)
            temp.setCity(AESUtil.encrypt(newData.getCity()));
        if (newData.getAddress() != null)
            temp.setAddress(AESUtil.encrypt(newData.getAddress()));
        if (newData.getAge() > 0) temp.setAge(newData.getAge());
        if (newData.getRole() != null) temp.setRole(newData.getRole());
        userRepo.save(temp);
        return 0;
    }

    
    @Override
    public int deleteById(Long id) {
        Optional<User> found = userRepo.findById(id);
        if (found.isPresent()) {
            userRepo.delete(found.get());
            return 0;
        }
        return 1;
    }

    
    public int deleteByUsername(String username) {
       
        Optional<User> found = userRepo.findByUsername(AESUtil.encrypt(username));
        if (found.isPresent()) {
            userRepo.delete(found.get());
            return 0;
        }
        return 1;
    }

    

    @Override
    public long count() { return userRepo.count(); }

    @Override
    public boolean exist(Long id) { return userRepo.existsById(id); }

    public boolean findUsernameAlreadyTaken(String username) {
        return userRepo.findByUsername(AESUtil.encrypt(username)).isPresent();
    }
}