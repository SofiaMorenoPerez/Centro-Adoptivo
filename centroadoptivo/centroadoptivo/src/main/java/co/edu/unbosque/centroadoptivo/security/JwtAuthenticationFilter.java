package co.edu.unbosque.centroadoptivo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de autenticación JWT que intercepta cada petición HTTP entrante.
 * Valida el token JWT del encabezado {@code Authorization} y establece
 * el contexto de seguridad si el token es válido.
 *
 * <p>El filtro sigue este flujo:
 * <ol>
 *   <li>Si la ruta empieza por {@code /auth/} se omite la validación</li>
 *   <li>Si no hay encabezado {@code Authorization} o no empieza por {@code Bearer }
 *       se continúa sin autenticar</li>
 *   <li>Si el token es malformado o expirado se responde con 401 inmediatamente</li>
 *   <li>Si el token es válido se establece la autenticación en el contexto de seguridad</li>
 * </ol>
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor del filtro de autenticación JWT.
     *
     * @param jwtUtil            utilidad para operaciones con tokens JWT
     * @param userDetailsService servicio para cargar los detalles del usuario
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil,
            UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Intercepta cada petición HTTP para validar el token JWT.
     * Si el token es válido establece la autenticación en el
     * {@link SecurityContextHolder} para que Spring Security
     * pueda autorizar el acceso a los endpoints protegidos.
     *
     * @param request     petición HTTP entrante
     * @param response    respuesta HTTP saliente
     * @param filterChain cadena de filtros a continuar
     * @throws ServletException si ocurre un error en el filtro
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(7);
        String username;
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Token inválido o expirado\"}");
            return;
        }

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails =
                    this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}