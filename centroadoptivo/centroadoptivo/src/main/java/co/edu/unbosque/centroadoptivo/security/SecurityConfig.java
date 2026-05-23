package co.edu.unbosque.centroadoptivo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth

                // ── Público ──────────────────────────────────────────
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/usuario/create", "/usuario/createjson").permitAll()

                // ── USER y ADMIN: ver y editar su propio perfil ───────
                .requestMatchers(
                    "/usuario/getbyid/**",
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

                // ── Solo ADMIN: resto de gestión de usuarios ──────────
                .requestMatchers("/usuario/**").hasRole("ADMIN")

                // ── Cualquier otra cosa requiere autenticación ────────
                .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
       
        config.setExposedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}