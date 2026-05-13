package co.edu.unbosque.centroadoptivo.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.centroadoptivo.entity.Usuario;
import co.edu.unbosque.centroadoptivo.repository.UsuarioRepository;

import org.springframework.context.annotation.Configuration;
@Configuration
public class LoadDatabase {
	
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {

		return args -> {
		      Optional<Usuario> found = usuarioRepo.findByUsername("admin");
		      if (found.isPresent()) {
		        log.info("El administrador ya existe, omitiendo la creación del administrador...");
		      } else {
		        Usuario adminUser = new Usuario("admin", passwordEncoder.encode("1234567890"), Usuario.Rol.ADMIN);
		        usuarioRepo.save(adminUser);
		        log.info("Precargando usuario administrador");
		      }
		      Optional<Usuario> found2 = usuarioRepo.findByUsername("usuarionormal");
		      if (found2.isPresent()) {
		        log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
		      } else {
		        Usuario usuarioNormal =
		            new Usuario("normaluser", passwordEncoder.encode("1234567890"), Usuario.Rol.USUARIO);
		        usuarioRepo.save(usuarioNormal);
		        log.info("Precargando usuario normal");
		      }
		    };
		  }
		}

