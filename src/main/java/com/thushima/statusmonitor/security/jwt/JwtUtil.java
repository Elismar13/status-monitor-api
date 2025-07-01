package com.thushima.statusmonitor.security.jwt;

import com.thushima.statusmonitor.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String ROLES_CLAIM = "roles";
    private static final String USER_ID_CLAIM = "userId";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTokenExpiration);
    }

    private String buildToken(User user, long expiration) {
        return Jwts.builder()
                .setSubject(user.getEmail().value())
                .claim(USER_ID_CLAIM, user.getId().value())
                .claim(ROLES_CLAIM, user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (ExpiredJwtException ex) {
                logger.warn("Expired JWT token: {}", ex.getMessage());
                throw new SecurityException("Expired JWT token");
            } catch (UnsupportedJwtException ex) {
                logger.warn("Unsupported JWT token: {}", ex.getMessage());
                throw new SecurityException("Unsupported JWT token");
            } catch (MalformedJwtException ex) {
                logger.warn("Invalid JWT token: {}", ex.getMessage());
                throw new SecurityException("Invalid JWT token");
            } catch (SignatureException ex) {
                logger.warn("Invalid JWT signature: {}", ex.getMessage());
                throw new SecurityException("Invalid JWT signature");
            } catch (IllegalArgumentException ex) {
                logger.warn("JWT claims string is empty: {}", ex.getMessage());
                throw new SecurityException("JWT claims string is empty");
            }
        });
    }

    public Mono<String> extractUsername(String token) {
        return validateToken(token)
                .map(Claims::getSubject)
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<Boolean> isTokenExpired(String token) {
        return validateToken(token)
                .map(claims -> claims.getExpiration().before(new Date()))
                .onErrorReturn(true);
    }

    @SuppressWarnings("unchecked")
    public Mono<Authentication> getAuthentication(String token) {
        return validateToken(token)
                .map(claims -> {
                    String username = claims.getSubject();
                    List<String> roles = claims.get(ROLES_CLAIM, List.class);

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                        
                    return new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
                });
    }
}