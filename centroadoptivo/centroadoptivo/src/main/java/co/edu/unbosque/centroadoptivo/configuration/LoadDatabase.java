package co.edu.unbosque.centroadoptivo.configuration;

import co.edu.unbosque.centroadoptivo.entity.Usuario;
import co.edu.unbosque.centroadoptivo.repository.UsuarioRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración para cargar datos iniciales en la base de datos. Crea usuarios
 * predeterminados (administrador y usuario normal) al iniciar la aplicación si estos no existen
 * previamente.
 */
@Configuration
public class LoadDatabase {

    /** Logger para registrar mensajes durante la carga de datos. */
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${user.password}")
    private String userPassword;

    /**
     * Inicializa la base de datos con usuarios predeterminados. Crea un usuario administrador y un
     * usuario normal si no existen.
     *
     * @param usuarioRepo Repositorio de usuarios para acceder a la base de datos
     * @param passwordEncoder Codificador de contraseñas para encriptar las contraseñas de los
     *     usuarios
     * @return Un CommandLineRunner que se ejecuta al iniciar la aplicación
     */
    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<Usuario> found = usuarioRepo.findByUsername("admin");
            if (found.isPresent()) {
                log.info("El administrador ya existe, omitiendo la creación del administrador...");
            } else {
                Usuario adminUser = new Usuario("admin", passwordEncoder.encode(adminPassword), Usuario.Rol.ADMIN);
                usuarioRepo.save(adminUser);
                log.info("Precargando usuario administrador");
            }
            Optional<Usuario> found2 = usuarioRepo.findByUsername("normaluser");
            if (found2.isPresent()) {
                log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
            } else {
                Usuario normalUser = new Usuario("normaluser", passwordEncoder.encode(userPassword), Usuario.Rol.USUARIO);
                usuarioRepo.save(normalUser);
                log.info("Precargando usuario normal");
            }
        };
    }
}