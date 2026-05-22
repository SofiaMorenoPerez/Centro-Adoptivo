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
import co.edu.unbosque.centroadoptivo.util.AESUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());

        // Username en texto plano para el token
        String usernameParaToken = userDetails.getUsername();

        if (userDetails instanceof User) {
            User user = (User) userDetails;
            claims.put("role", user.getRole().name());
            // El username en BD está encriptado — lo desencriptamos para el token
            try {
                usernameParaToken = AESUtil.decrypt(user.getUsername());
            } catch (Exception e) {
                usernameParaToken = user.getUsername();
            }
        }

        return createToken(claims, usernameParaToken);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String usernameEnToken = extractUsername(token); 
  
        String usernameDesencriptado;
        try {
            usernameDesencriptado = AESUtil.decrypt(userDetails.getUsername());
        } catch (Exception e) {
            usernameDesencriptado = userDetails.getUsername();
        }
        return (usernameEnToken.equals(usernameDesencriptado) && !isTokenExpired(token));
    }
}