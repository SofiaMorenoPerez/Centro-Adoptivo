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
import co.edu.unbosque.centroadoptivo.util.AESUtil;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${user.password}")
    private String userPassword;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {

        
            Optional<User> found = userRepo.findByUsername(AESUtil.encrypt("admin1"));
            if (found.isPresent()) {
                log.info("El administrador ya existe, omitiendo creación...");
            } else {
              
                User adminUser = new User(
                    AESUtil.encrypt("admin1"),
                    passwordEncoder.encode(adminPassword),
                    User.Role.ADMIN
                );
                userRepo.save(adminUser);
                log.info("Precargando usuario administrador");
            }

            Optional<User> found2 = userRepo.findByUsername(AESUtil.encrypt("normaluser1"));
            if (found2.isPresent()) {
                log.info("El usuario normal ya existe, omitiendo creación...");
            } else {
                User normalUser = new User(
                    AESUtil.encrypt("normaluser1"),
                    passwordEncoder.encode(userPassword),
                    User.Role.USER
                );
                userRepo.save(normalUser);
                log.info("Precargando usuario normal");
            }
        };
    }
}