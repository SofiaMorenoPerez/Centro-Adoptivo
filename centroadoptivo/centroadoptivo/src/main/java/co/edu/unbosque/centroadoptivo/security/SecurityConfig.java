package co.edu.unbosque.centroadoptivo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import java.util.List;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de seguridad de la aplicación.
 * Define las reglas de autorización por rol, la política de sesiones sin estado
 * y el proveedor de autenticación basado en JWT.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Filtro que intercepta y valida el token JWT en cada petición. */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /** Servicio que carga los detalles del usuario para la autenticación. */
    private final UserDetailsService userDetailsService;

    /**
     * Constructor que inyecta las dependencias necesarias para la configuración de seguridad.
     *
     * @param jwtAuthFilter    filtro de autenticación JWT
     * @param userDetailsService servicio de carga de detalles del usuario
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
            UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Define la cadena de filtros de seguridad HTTP.
     * Configura las rutas públicas, las rutas protegidas por rol y la política de sesiones.
     *
     * @param http objeto de configuración de seguridad HTTP
     * @return la cadena de filtros configurada
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ── Público ───────────────────────────────────────────
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()

                // ── USER y ADMIN: perfil propio ───────────────────────
                .requestMatchers(
                    "/usuario/perfil/**",
                    "/usuario/editar/**"
                ).hasAnyRole("USER", "ADMIN")

                // ── USER y ADMIN: animales ────────────────────────────
                .requestMatchers(
                    "/animal/getall",
                    "/animal/getbyid/**",
                    "/animal/registrar",
                    "/animal/mispublicaciones",
                    "/animal/publicador/**"
                ).hasAnyRole("USER", "ADMIN")

                // ── USER y ADMIN: solicitudes ─────────────────────────
                .requestMatchers(
                    "/solicitud/crear",
                    "/solicitud/missolicitudes",
                    "/solicitud/animal/**"
                ).hasAnyRole("USER", "ADMIN")

                // ── USER y ADMIN: notificaciones ──────────────────────
                .requestMatchers("/notificacion/**").hasAnyRole("USER", "ADMIN")

                // ── Solo ADMIN: aprobar/rechazar solicitudes ──────────
                .requestMatchers(
                    "/solicitud/aprobar/**",
                    "/solicitud/rechazar/**",
                    "/solicitud/pendientes"
                ).hasRole("ADMIN")

                // ── Solo ADMIN: gestión de animales ───────────────────
                .requestMatchers(
                    "/animal/getallAdmin",
                    "/animal/delete/**",
                    "/animal/update/**"
                ).hasRole("ADMIN")

                // ── Solo ADMIN: gestión completa de usuarios ──────────
                .requestMatchers("/usuario/**").hasRole("ADMIN")

                // ── Cualquier otra cosa requiere autenticación ─────────
                .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define el proveedor de autenticación que verifica las credenciales
     * del usuario contra la base de datos usando BCrypt.
     *
     * @return proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expone el {@link AuthenticationManager} como bean de Spring
     * para ser usado en el proceso de autenticación del login.
     *
     * @param config configuración de autenticación de Spring
     * @return el gestor de autenticación
     * @throws Exception si ocurre un error al obtener el gestor
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el codificador de contraseñas usando el algoritmo BCrypt.
     *
     * @return codificador de contraseñas BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Define la configuración global de CORS.
     * Permite peticiones desde el frontend en Netlify hacia el backend en Tomcat.
     * Cuando se obtenga la URL real de Netlify, reemplazar el origen correspondiente.
     *
     * @return fuente de configuración CORS aplicada a todos los endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
            "https://tu-app.netlify.app"   
        ));

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));

        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}