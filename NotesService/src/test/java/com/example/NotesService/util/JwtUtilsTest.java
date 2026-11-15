package com.example.NotesService.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.NotesService.config.NotesPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.Test;

class JwtUtilsTest {

    private static final String SECRET = "notes-service-test-secret-key-at-least-32-bytes";

    private final JwtUtils jwtUtils = new JwtUtils(SECRET);

    @Test
    void extractsUserIdFromSignedAccessToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .claim("user_id", 51L)
                .claim("token_type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        NotesPrincipal principal = jwtUtils.parseAccessToken(token);

        assertEquals(51L, principal.userId());
    }

    @Test
    void rejectsPasswordResetTokensAsNotesAccessTokens() {
        String token = Jwts.builder()
                .claim("user_id", 51L)
                .claim("token_type", "password_reset")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(JwtException.class, () -> jwtUtils.parseAccessToken(token));
    }
}
