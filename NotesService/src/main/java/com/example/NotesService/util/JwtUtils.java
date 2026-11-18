package com.example.NotesService.util;

import com.example.NotesService.config.NotesPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public NotesPrincipal parseAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!"access".equals(claims.get("token_type", String.class))) {
            throw new JwtException("Token is not an access token");
        }
        Object userIdClaim = claims.get("user_id");
        if (!(userIdClaim instanceof Number userId) || userId.longValue() <= 0) {
            throw new JwtException("Token has no valid user ID");
        }
        String email = claims.getSubject();
        if (email == null || email.isBlank()) {
            throw new JwtException("Token has no valid user email");
        }
        return new NotesPrincipal(userId.longValue(), email);
    }
}
