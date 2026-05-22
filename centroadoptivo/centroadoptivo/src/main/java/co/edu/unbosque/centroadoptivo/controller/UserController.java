package co.edu.unbosque.centroadoptivo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.centroadoptivo.dto.UserDTO;
import co.edu.unbosque.centroadoptivo.entity.User.Role;
import co.edu.unbosque.centroadoptivo.service.AuditoriaService;
import co.edu.unbosque.centroadoptivo.service.UserService;
import co.edu.unbosque.centroadoptivo.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = { "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administrar usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userServ;

    @Autowired
    private AuditoriaService auditoriaServ;

    public UserController() {}

    private String getUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    

    @Operation(summary = "Crear usuario (JSON)")
    @PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createWithJSON(@RequestBody UserDTO newUser) {
        int status = userServ.create(newUser);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "CREATE_USER",
                "Creó usuario con username=" + newUser.getUsername(), true);
            return new ResponseEntity<>("Usuario creado exitosamente", HttpStatus.CREATED);
        }
        auditoriaServ.registrar(getUsuarioActual(), "CREATE_USER",
            "Intento fallido de crear usuario con username=" + newUser.getUsername()
            + " | código=" + status, false);
        if (status == 1)  return new ResponseEntity<>("El nombre de usuario no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 2)  return new ResponseEntity<>("La contraseña no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 3)  return new ResponseEntity<>("El email no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 4)  return new ResponseEntity<>("El nombre completo no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 5)  return new ResponseEntity<>("El teléfono no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 6)  return new ResponseEntity<>("La ciudad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 7)  return new ResponseEntity<>("La dirección no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 8)  return new ResponseEntity<>("La edad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 9)  return new ResponseEntity<>("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
        else if (status == 10) return new ResponseEntity<>("El email ya está en uso", HttpStatus.CONFLICT);
        else return new ResponseEntity<>("Error al crear usuario", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Operation(summary = "Crear usuario (parámetros)")
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam String username, @RequestParam String password,
            @RequestParam(required = false) String fullName, @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone, @RequestParam(required = false) String city,
            @RequestParam(required = false) String address, @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Role role) {
        UserDTO newUser = new UserDTO();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setCity(city);
        newUser.setAddress(address);
        if (age != null) newUser.setAge(age);
        newUser.setRole(role);
        int status = userServ.create(newUser);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "CREATE_USER",
                "Creó usuario con username=" + username, true);
            return new ResponseEntity<>("Usuario creado exitosamente", HttpStatus.CREATED);
        }
        auditoriaServ.registrar(getUsuarioActual(), "CREATE_USER",
            "Intento fallido de crear usuario con username=" + username
            + " | código=" + status, false);
        if (status == 1)  return new ResponseEntity<>("El nombre de usuario no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 2)  return new ResponseEntity<>("La contraseña no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 3)  return new ResponseEntity<>("El email no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 4)  return new ResponseEntity<>("El nombre completo no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 5)  return new ResponseEntity<>("El teléfono no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 6)  return new ResponseEntity<>("La ciudad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 7)  return new ResponseEntity<>("La dirección no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 8)  return new ResponseEntity<>("La edad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 9)  return new ResponseEntity<>("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
        else if (status == 10) return new ResponseEntity<>("El email ya está en uso", HttpStatus.CONFLICT);
        else return new ResponseEntity<>("Error al crear usuario", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    

    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> list = userServ.getAll();
        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        UserDTO found = userServ.getById(id);
        if (found != null) return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Verificar existencia de usuario")
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean found = userServ.exist(id);
        if (found) return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Contar usuarios")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        Long count = userServ.count();
        if (count == 0) return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
    }

  

    @Operation(summary = "Actualizar usuario (JSON)")
    @PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateWithJSON(@RequestParam Long id, @RequestBody UserDTO newUser) {
        int status = userServ.updateById(id, newUser);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "UPDATE_USER",
                "Actualizó usuario con id=" + id, true);
            return new ResponseEntity<>("Usuario actualizado exitosamente", HttpStatus.ACCEPTED);
        }
        auditoriaServ.registrar(getUsuarioActual(), "UPDATE_USER",
            "Intento fallido de actualizar usuario con id=" + id
            + " | código=" + status, false);
        if (status == 1)  return new ResponseEntity<>("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
        else if (status == 2)  return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        else if (status == 3)  return new ResponseEntity<>("La contraseña no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 4)  return new ResponseEntity<>("El email no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 5)  return new ResponseEntity<>("La edad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 6)  return new ResponseEntity<>("El nombre completo no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 7)  return new ResponseEntity<>("El teléfono no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 8)  return new ResponseEntity<>("La ciudad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 9)  return new ResponseEntity<>("La dirección no es válida", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("Error al actualizar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Operation(summary = "Actualizar usuario (parámetros)")
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestParam Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Role role) {
        UserDTO newUser = new UserDTO();
        newUser.setUsername(username);
        newUser.setPassword(password);
        if (role != null) newUser.setRole(role);
        int status = userServ.updateById(id, newUser);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "UPDATE_USER",
                "Actualizó usuario con id=" + id, true);
            return new ResponseEntity<>("Usuario actualizado exitosamente", HttpStatus.ACCEPTED);
        }
        auditoriaServ.registrar(getUsuarioActual(), "UPDATE_USER",
            "Intento fallido de actualizar usuario con id=" + id
            + " | código=" + status, false);
        if (status == 1)  return new ResponseEntity<>("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
        else if (status == 2)  return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        else if (status == 3)  return new ResponseEntity<>("La contraseña no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 4)  return new ResponseEntity<>("El email no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 5)  return new ResponseEntity<>("La edad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 6)  return new ResponseEntity<>("El nombre completo no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 7)  return new ResponseEntity<>("El teléfono no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 8)  return new ResponseEntity<>("La ciudad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 9)  return new ResponseEntity<>("La dirección no es válida", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("Error al actualizar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @Operation(summary = "Eliminar usuario por ID")
    @DeleteMapping("/deletebyid/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        UserDTO target = userServ.getById(id);
        String targetUsername = (target != null) ? target.getUsername() : "id=" + id;
        int status = userServ.deleteById(id);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "DELETE_USER",
                "Eliminó usuario con username=" + targetUsername, true);
            return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
        }
        auditoriaServ.registrar(getUsuarioActual(), "DELETE_USER",
            "Intento fallido de eliminar usuario con username=" + targetUsername
            + " — no encontrado", false);
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Eliminar usuario por username")
    @DeleteMapping("/deletebyusername")
    public ResponseEntity<String> deleteByUsername(@RequestParam String username) {
        int status = userServ.deleteByUsername(username);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "DELETE_USER",
                "Eliminó usuario con username=" + username, true);
            return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
        }
        auditoriaServ.registrar(getUsuarioActual(), "DELETE_USER",
            "Intento fallido de eliminar usuario con username=" + username
            + " — no encontrado", false);
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
    }
}