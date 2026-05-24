package co.edu.unbosque.centroadoptivo.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import co.edu.unbosque.centroadoptivo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para la generación, validación y extracción de información
 * de tokens JWT (JSON Web Token) usados en la autenticación del sistema.
 *
 * <p>Los tokens tienen una validez de 24 horas desde su generación
 * y están firmados con el algoritmo HMAC-SHA256 usando la clave
 * configurada en {@code application.properties}.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Component
public class JwtUtil {

    /** Tiempo de validez del token en milisegundos (24 horas). */
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Obtiene la clave de firma HMAC-SHA a partir del secreto configurado.
     *
     * @return clave de firma para firmar y verificar tokens JWT
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extrae el username (subject) del token JWT.
     *
     * @param token token JWT del que extraer el username
     * @return username contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token token JWT del que extraer la expiración
     * @return fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae el rol del usuario contenido en el token JWT.
     *
     * @param token token JWT del que extraer el rol
     * @return nombre del rol del usuario (ej: USER, ADMIN)
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extrae un claim específico del token JWT usando una función resolutora.
     *
     * @param <T>            tipo del claim a extraer
     * @param token          token JWT del que extraer el claim
     * @param claimsResolver función que determina qué claim extraer
     * @return valor del claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    /**
     * Extrae todos los claims del token JWT verificando su firma.
     *
     * @param token token JWT a parsear
     * @return todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param token token JWT a verificar
     * @return {@code true} si el token está expirado, {@code false} si no
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * Incluye las autoridades y el rol del usuario como claims adicionales.
     * El username se guarda en texto plano como subject del token.
     *
     * @param userDetails detalles del usuario autenticado
     * @return token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            claims.put("role", user.getRole().name());
        }
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea y firma el token JWT con los claims y subject indicados.
     *
     * @param claims  mapa de claims adicionales a incluir en el token
     * @param subject username del usuario como subject del token
     * @return token JWT firmado y compactado
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(
                        System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida que el token JWT sea auténtico y no haya expirado.
     * Compara el username del token con el del usuario autenticado
     * directamente sin necesidad de desencriptación.
     *
     * @param token       token JWT a validar
     * @param userDetails detalles del usuario contra quien validar el token
     * @return {@code true} si el token es válido, {@code false} si no
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}