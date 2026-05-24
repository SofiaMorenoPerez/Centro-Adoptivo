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
import org.springframework.web.bind.annotation.PatchMapping;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de usuarios.
 * <p>
 * Expone endpoints para consultar, crear, actualizar, eliminar usuarios
 * y gestionar roles. Algunos endpoints están restringidos a usuarios con rol ADMIN.
 * Requiere autenticación mediante JWT.
 * </p>
 */
@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
@Transactional
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administrar usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    /** Servicio de lógica de negocio para usuarios. */
    @Autowired
    private UserService userServ;

    /** Servicio de auditoría para registrar acciones del sistema. */
    @Autowired
    private AuditoriaService auditoriaServ;

    /** Constructor por defecto. */
    public UserController() {}

    /**
     * Obtiene el nombre del usuario autenticado en el contexto de seguridad actual.
     *
     * @return nombre de usuario autenticado
     */
    private String getUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Verifica si el usuario autenticado tiene rol de administrador.
     *
     * @return {@code true} si el usuario es ADMIN, {@code false} en caso contrario
     */
    private boolean esAdmin() {
        return SecurityContextHolder.getContext()
            .getAuthentication().getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // ── Perfil propio (USER y ADMIN) ──────────────────────────────────────────

    /**
     * Obtiene el perfil de un usuario por su identificador.
     * <p>
     * Un usuario con rol USER solo puede consultar su propio perfil.
     * </p>
     *
     * @param id identificador del usuario
     * @return el {@link UserDTO} encontrado, 403 si no tiene permisos, o 404 si no existe
     */
    @Operation(summary = "Ver mi perfil")
    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> getPerfil(@PathVariable Long id) {
        UserDTO found = userServ.getById(id);
        if (found == null)
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);

        if (!esAdmin() && !found.getUsername().equals(getUsuarioActual()))
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /**
     * Actualiza el perfil de un usuario por su identificador.
     * <p>
     * Un usuario con rol USER solo puede editar su propio perfil y no puede cambiar su rol.
     * </p>
     *
     * @param id      identificador del usuario a editar
     * @param newUser DTO con los nuevos datos del usuario
     * @return mensaje de éxito, 403 si no tiene permisos, o un mensaje de error según la validación
     */
    @Operation(summary = "Editar mi perfil")
    @PutMapping(path = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editarPerfil(@PathVariable Long id,
            @RequestBody UserDTO newUser) {
        UserDTO found = userServ.getById(id);
        if (found == null)
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);

        if (!esAdmin() && !found.getUsername().equals(getUsuarioActual()))
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);

        if (!esAdmin()) newUser.setRole(null);

        int status = userServ.updateById(id, newUser);
        if (status == 0) {
            auditoriaServ.registrar(getUsuarioActual(), "EDITAR_PERFIL",
                "Editó perfil id=" + id, true);
            return new ResponseEntity<>("Perfil actualizado exitosamente", HttpStatus.OK);
        }
        auditoriaServ.registrar(getUsuarioActual(), "EDITAR_PERFIL",
            "Intento fallido de editar perfil id=" + id + " | código=" + status, false);
        if (status == 1)  return new ResponseEntity<>("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
        else if (status == 2)  return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        else if (status == 3)  return new ResponseEntity<>("La contraseña no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 4)  return new ResponseEntity<>("El email no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 5)  return new ResponseEntity<>("La edad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 6)  return new ResponseEntity<>("El nombre completo no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 7)  return new ResponseEntity<>("El teléfono no es válido", HttpStatus.BAD_REQUEST);
        else if (status == 8)  return new ResponseEntity<>("La ciudad no es válida", HttpStatus.BAD_REQUEST);
        else if (status == 9)  return new ResponseEntity<>("La dirección no es válida", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("Error al actualizar perfil", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ── Solo ADMIN: gestión completa de usuarios ──────────────────────────────

    /**
     * Crea un nuevo usuario a partir de un cuerpo JSON (uso exclusivo de ADMIN).
     * <p>
     * Si el ADMIN especifica un rol, este se asigna en una actualización posterior a la creación.
     * </p>
     *
     * @param newUser DTO con los datos del nuevo usuario
     * @return mensaje de éxito con 201, o un mensaje de error según la validación
     */
    @Operation(summary = "Crear usuario (JSON)")
    @PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createWithJSON(@RequestBody UserDTO newUser) {
        int status = userServ.create(newUser);
        if (status == 0) {
            if (newUser.getRole() != null) {
                UserDTO creado = userServ.getByUsername(newUser.getUsername());
                if (creado != null) {
                    UserDTO rolDTO = new UserDTO();
                    rolDTO.setRole(newUser.getRole());
                    userServ.updateById(creado.getId(), rolDTO);
                }
            }
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

    /**
     * Obtiene la lista de todos los usuarios registrados en el sistema.
     *
     * @return lista de {@link UserDTO}, o 204 si no hay usuarios
     */
    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> list = userServ.getAll();
        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return el {@link UserDTO} encontrado, o 404 si no existe
     */
    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        UserDTO found = userServ.getById(id);
        if (found != null) return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
    }

    /**
     * Verifica si existe un usuario con el identificador indicado.
     *
     * @param id identificador del usuario
     * @return {@code true} si existe, o 204 si no existe
     */
    @Operation(summary = "Verificar existencia de usuario")
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean found = userServ.exist(id);
        if (found) return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
    }

    /**
     * Retorna el total de usuarios registrados en el sistema.
     *
     * @return cantidad de usuarios, o 204 si no hay ninguno
     */
    @Operation(summary = "Contar usuarios")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        Long count = userServ.count();
        if (count == 0) return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
    }

    /**
     * Actualiza los datos de un usuario existente a partir de un cuerpo JSON.
     *
     * @param id      identificador del usuario a actualizar
     * @param newUser DTO con los nuevos datos del usuario
     * @return mensaje de éxito, o un mensaje de error según la validación
     */
    @Operation(summary = "Actualizar usuario (JSON)")
    @PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateWithJSON(@RequestParam Long id,
            @RequestBody UserDTO newUser) {
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

    /**
     * Elimina un usuario del sistema por su identificador.
     *
     * @param id identificador del usuario a eliminar
     * @return mensaje de éxito, o 404 si el usuario no existe
     */
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

    /**
     * Elimina un usuario del sistema por su nombre de usuario.
     * <p>
     * El username se recibe en el cuerpo de la petición para no exponerlo en la URL.
     * </p>
     *
     * @param userDTO DTO que contiene el username del usuario a eliminar
     * @return mensaje de éxito, o 404 si el usuario no existe
     */
    @Operation(summary = "Eliminar usuario por username")
    @DeleteMapping("/deletebyusername")
    public ResponseEntity<String> deleteByUsername(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
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

    /**
     * Cambia el rol de un usuario existente (uso exclusivo de ADMIN).
     *
     * @param id  identificador del usuario
     * @param rol nombre del nuevo rol a asignar (debe coincidir con los valores de {@link Role})
     * @return mensaje de éxito, o 500 si ocurre un error al actualizar
     */
    @Operation(summary = "Cambiar rol de usuario (ADMIN)")
    @PatchMapping("/rol/{id}")
    public ResponseEntity<String> cambiarRol(@PathVariable Long id, @RequestParam String rol) {
        UserDTO newData = new UserDTO();
        newData.setRole(Role.valueOf(rol));
        int status = userServ.updateById(id, newData);
        if (status == 0) return new ResponseEntity<>("Rol actualizado", HttpStatus.OK);
        return new ResponseEntity<>("Error al actualizar rol", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}