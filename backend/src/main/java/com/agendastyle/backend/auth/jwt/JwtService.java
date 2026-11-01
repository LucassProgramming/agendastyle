package com.agendastyle.backend.auth.jwt;

import com.agendastyle.backend.auth.Role;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.Clock;
import java.util.Base64;

@Service
public class JwtService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int MINIMUM_SECRET_BYTES = 32;

    private final NimbusJwtEncoder jwtEncoder;
    private final NimbusJwtDecoder jwtDecoder;
    private final Duration expiration;
    private final Clock clock;

    @Autowired
    public JwtService(@Value("${app.jwt.secret}") String encodedSecret, @Value("${app.jwt.expiration}") Duration expiration) {
        this(encodedSecret, expiration, Clock.systemUTC());
    }

    JwtService(String encodedSecret, Duration expiration, Clock clock) {
        byte[] secretBytes = decodeSecret(encodedSecret);
        SecretKey secretKey = new SecretKeySpec(secretBytes, ALGORITHM);
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        this.expiration = expiration;
        this.clock = clock;
    }

    public String generateToken(String email, Role role) {
        Instant issuedAt = Instant.now(clock);
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(email).claim("role", role.name()).issuedAt(issuedAt).expiresAt(issuedAt.plus(expiration)).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return expiration.toSeconds();
    }

    public Jwt parseToken(String token) {
        return jwtDecoder.decode(token);
    }

    private byte[] decodeSecret(String encodedSecret) {
        byte[] secretBytes;
        try {
            secretBytes = Base64.getDecoder().decode(encodedSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT secret must be valid Base64", exception);
        }
        if (secretBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException("JWT secret must contain at least 32 bytes");
        }
        return secretBytes;
    }
}
