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

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:8081", "*" })
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (login y registro)")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuditoriaService auditoriaService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UserService userService, AuditoriaService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.auditoriaService = auditoriaService;
    }

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

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO registerRequest) {
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

    private static class AuthResponse {
        private final String token;
        private final String role;
        private final Long id;

        public AuthResponse(String token, String role, Long id) {
            this.token = token;
            this.role = role;
            this.id = id;
        }

        public String getToken() { return token; }
        public String getRole()  { return role; }
        public Long getId()      { return id; }
    }
}