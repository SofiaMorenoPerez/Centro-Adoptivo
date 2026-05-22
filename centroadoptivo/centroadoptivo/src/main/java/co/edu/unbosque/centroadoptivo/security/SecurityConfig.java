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
                .authorizeHttpRequests(auth ->
                
				                auth.requestMatchers("/auth/**")
				                .permitAll()
				                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
				                .permitAll()
                        

                                
                                .requestMatchers(
                                    "/usuario/create",
                                    "/usuario/createjson"
                                ).permitAll()

                                
                                .requestMatchers(
                                        "/animal/getall",
                                        "/animal/getbyid/**",
                                        "/animal/registrar",
                                        "/animal/mispublicaciones",
                                        "/animal/publicador/**"
                                    ).hasAnyRole("USER", "ADMIN")

                                 
                                    .requestMatchers(
                                        "/usuario/getbyid/**",
                                        "/usuario/perfil/**",
                                        "/usuario/editar/**"
                                    ).hasAnyRole("USER", "ADMIN")

                       
                                    .requestMatchers(
                                        "/solicitud/crear",
                                        "/solicitud/missolicitudes",
                                        "/solicitud/animal/**"
                                    ).hasAnyRole("USER", "ADMIN")

                        
                                    .requestMatchers("/notificacion/**").hasAnyRole("USER", "ADMIN")

                                    
                                    .requestMatchers(
                                        "/solicitud/aprobar/**",
                                        "/solicitud/rechazar/**",
                                        "/solicitud/pendientes"
                                    ).hasRole("ADMIN")

                   
                                    .requestMatchers(
                                        "/animal/getallAdmin",
                                        "/animal/delete/**",
                                        "/animal/update/**"
                                    ).hasRole("ADMIN")

                 
                                    .requestMatchers("/usuario/**").hasRole("ADMIN")

                 
                                    .requestMatchers("/animal/**").hasRole("ADMIN")

                                  
                                    .anyRequest().authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}