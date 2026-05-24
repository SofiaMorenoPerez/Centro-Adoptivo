package co.edu.unbosque.centroadoptivo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.centroadoptivo.dto.UserDTO;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.security.JwtUtil;
import co.edu.unbosque.centroadoptivo.service.AuditoriaService;
import co.edu.unbosque.centroadoptivo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la autenticación de usuarios.
 * <p>
 * Expone endpoints para el inicio de sesión y el registro de nuevos usuarios.
 * </p>
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (login y registro)")
public class AuthController {

    /** Gestor de autenticación de Spring Security. */
    private final AuthenticationManager authenticationManager;

    /** Utilidad para generación y validación de tokens JWT. */
    private final JwtUtil jwtUtil;

    /** Servicio de lógica de negocio para usuarios. */
    private final UserService userService;

    /** Servicio de auditoría para registrar acciones del sistema. */
    private final AuditoriaService auditoriaService;

    /**
     * Constructor del controlador de autenticación.
     *
     * @param authenticationManager gestor de autenticación de Spring Security
     * @param jwtUtil               utilidad para manejo de tokens JWT
     * @param userService           servicio de usuarios
     * @param auditoriaService      servicio de auditoría
     */
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UserService userService, AuditoriaService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Autentica un usuario y retorna un token JWT junto con su rol e identificador.
     *
     * @param loginRequest DTO con las credenciales del usuario (username y password)
     * @return {@link AuthResponse} con el token, rol e id del usuario autenticado,
     *         o 401 si las credenciales son inválidas
     */
    @Operation(summary = "Iniciar sesión de usuario")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            String role = null;
            Long userId = null;
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                role = user.getRole().name();
                userId = user.getId();
            }

            auditoriaService.registrar(loginRequest.getUsername(), "LOGIN",
                "Inicio de sesión exitoso", true);

            return ResponseEntity.ok(new AuthResponse(jwt, role, userId));

        } catch (AuthenticationException e) {
            auditoriaService.registrar(loginRequest.getUsername(), "LOGIN",
                "Intento de login fallido", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
        }
    }

    /**
     * Registra un nuevo usuario en el sistema con rol USER por defecto.
     *
     * @param registerRequest DTO con los datos del nuevo usuario
     * @return 201 si fue registrado exitosamente, o un mensaje de error con el
     *         código HTTP correspondiente según el tipo de validación fallida
     */
    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO registerRequest) {

        registerRequest.setRole(null);

        int result = userService.create(registerRequest);
        if (result == 0)  return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
        else if (result == 1)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario no es válido");
        else if (result == 2)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña no es válida");
        else if (result == 3)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email no es válido");
        else if (result == 4)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre completo no es válido");
        else if (result == 5)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El teléfono no es válido");
        else if (result == 6)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ciudad no es válida");
        else if (result == 7)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La dirección no es válida");
        else if (result == 8)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La edad no es válida — debe ser mayor de 18 años");
        else if (result == 9)  return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya está en uso");
        else if (result == 10) return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está en uso");
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario");
    }

    /**
     * Clase interna que representa la respuesta de autenticación exitosa.
     */
    private static class AuthResponse {

        /** Token JWT generado tras la autenticación. */
        private final String token;

        /** Rol del usuario autenticado. */
        private final String role;

        /** Identificador único del usuario autenticado. */
        private final Long id;

        /**
         * Constructor de la respuesta de autenticación.
         *
         * @param token token JWT generado
         * @param role  rol del usuario
         * @param id    identificador del usuario
         */
        public AuthResponse(String token, String role, Long id) {
            this.token = token;
            this.role = role;
            this.id = id;
        }

        /**
         * Retorna el token JWT.
         *
         * @return token JWT
         */
        public String getToken() { return token; }

        /**
         * Retorna el rol del usuario.
         *
         * @return rol del usuario
         */
        public String getRole()  { return role; }

        /**
         * Retorna el identificador del usuario.
         *
         * @return id del usuario
         */
        public Long getId()      { return id; }
    }
}