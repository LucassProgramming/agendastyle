package com.agendastyle.backend.auth.jwt;

import com.agendastyle.backend.auth.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import java.time.Duration;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private static final String DIFFERENT_SECRET = "ZmVkY2JhOTg3NjU0MzIxMGZlZGNiYTk4NzY1NDMyMTA=";

    @Test
    void shouldGenerateSignedTokenWithExpectedClaims() {
        JwtService jwtService = new JwtService(SECRET, Duration.ofHours(1));
        Instant beforeGeneration = Instant.now();

        Jwt jwt = jwtService.parseToken(jwtService.generateToken("ana@example.com", Role.CLIENTE));

        assertEquals("HS256", jwt.getHeaders().get("alg"));
        assertEquals("ana@example.com", jwt.getSubject());
        assertEquals("CLIENTE", jwt.getClaimAsString("role"));
        assertTrue(!jwt.getIssuedAt().isBefore(beforeGeneration.minusSeconds(1)));
        assertEquals(Duration.ofHours(1), Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()));
        assertEquals(3600L, jwtService.getExpirationSeconds());
    }

    @Test
    void shouldRejectTokenWhenSignatureDoesNotMatch() {
        JwtService jwtService = new JwtService(SECRET, Duration.ofHours(1));
        String token = jwtService.generateToken("ana@example.com", Role.CLIENTE);

        JwtService differentJwtService = new JwtService(DIFFERENT_SECRET, Duration.ofHours(1));
        assertThrows(JwtException.class, () -> differentJwtService.parseToken(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        Clock pastClock = Clock.fixed(Instant.now().minus(Duration.ofHours(2)), ZoneOffset.UTC);
        JwtService jwtService = new JwtService(SECRET, Duration.ofHours(1), pastClock);
        String token = jwtService.generateToken("ana@example.com", Role.CLIENTE);

        assertThrows(JwtException.class, () -> jwtService.parseToken(token));
    }

    @Test
    void shouldRejectInvalidBase64Secret() {
        assertThrows(IllegalStateException.class, () -> new JwtService("not-base64!", Duration.ofHours(1)));
    }

    @Test
    void shouldRejectShortSecret() {
        String shortSecret = Base64.getEncoder().encodeToString("too-short".getBytes());
        assertThrows(IllegalStateException.class, () -> new JwtService(shortSecret, Duration.ofHours(1)));
    }
}
