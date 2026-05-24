package co.edu.unbosque.centroadoptivo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;

/**
 * Implementación del servicio de detalles de usuario para la autenticación con Spring Security.
 * Carga la información del usuario desde la base de datos a partir de su nombre de usuario.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /** Repositorio de usuarios utilizado para buscar información de autenticación. */
    private final UserRepository userRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     *
     * @param userRepository repositorio de usuarios
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los detalles del usuario a partir de su nombre de usuario.
     * Este método es invocado por Spring Security durante el proceso de autenticación.
     *
     * @param username nombre de usuario a buscar
     * @return detalles del usuario encontrado
     * @throws UsernameNotFoundException si no existe un usuario con el nombre proporcionado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
    }
}