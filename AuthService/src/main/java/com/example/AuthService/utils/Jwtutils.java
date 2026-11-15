package com.example.AuthService.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import com.example.AuthService.expection.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Jwtutils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long tokenLifetimeMillis;

    @Value("${jwt.reset-expiration:900000}")
    private long resetTokenLifetimeMillis;

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("token_type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenLifetimeMillis))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateResetToken(String email, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("user_id", userId)
                .claim("token_type", "password_reset")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + resetTokenLifetimeMillis))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            return "access".equals(parseClaims(token).get("token_type", String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractUserIdFromResetToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (!"password_reset".equals(claims.get("token_type", String.class))) {
                throw new InvalidCredentialsException("Invalid password reset token");
            }
            Number userId = claims.get("user_id", Number.class);
            if (userId == null) {
                throw new InvalidCredentialsException("Invalid password reset token");
            }
            return userId.longValue();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidCredentialsException("Invalid password reset token");
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
