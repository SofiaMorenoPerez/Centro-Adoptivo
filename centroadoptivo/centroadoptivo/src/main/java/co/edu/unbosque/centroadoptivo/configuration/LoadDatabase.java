package co.edu.unbosque.centroadoptivo.configuration;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;

/**
 * Configuración de carga inicial de datos en la base de datos.
 * <p>
 * Crea usuarios predeterminados (administrador y usuario normal)
 * al iniciar la aplicación si aún no existen.
 * </p>
 */
@Configuration
public class LoadDatabase {

    /** Logger para registrar eventos de inicialización. */
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    /** Contraseña del administrador obtenida desde las propiedades de la aplicación. */
    @Value("${admin.password}")
    private String adminPassword;

    /** Contraseña del usuario normal obtenida desde las propiedades de la aplicación. */
    @Value("${user.password}")
    private String userPassword;

    /**
     * Inicializa la base de datos con usuarios predeterminados al arrancar la aplicación.
     * <p>
     * Crea un usuario administrador con username {@code admin3} y un usuario normal
     * con username {@code normaluser3}, solo si no existen previamente.
     * </p>
     *
     * @param userRepo        repositorio de usuarios
     * @param passwordEncoder codificador de contraseñas
     * @return un {@link CommandLineRunner} que ejecuta la lógica de inicialización
     */
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {

            Optional<User> found = userRepo.findByUsername("admin3");
            if (found.isPresent()) {
                log.info("El administrador ya existe, omitiendo creación...");
            } else {
                User adminUser = new User(
                    "admin3",
                    passwordEncoder.encode(adminPassword),
                    User.Role.ADMIN
                );
                userRepo.save(adminUser);
                log.info("Precargando usuario administrador");
            }

            Optional<User> found2 = userRepo.findByUsername("normaluser3");
            if (found2.isPresent()) {
                log.info("El usuario normal ya existe, omitiendo creación...");
            } else {
                User normalUser = new User(
                    "normaluser3",
                    passwordEncoder.encode(userPassword),
                    User.Role.USER
                );
                userRepo.save(normalUser);
                log.info("Precargando usuario normal");
            }
        };
    }
}