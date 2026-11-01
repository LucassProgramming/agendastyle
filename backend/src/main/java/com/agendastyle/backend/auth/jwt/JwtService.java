package com.agendastyle.backend.auth.jwt;

import com.agendastyle.backend.auth.Role;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int MINIMUM_SECRET_BYTES = 32;

    private final NimbusJwtEncoder jwtEncoder;
    private final Duration expiration;

    public JwtService(@Value("${app.jwt.secret}") String encodedSecret, @Value("${app.jwt.expiration}") Duration expiration) {
        byte[] secretBytes = decodeSecret(encodedSecret);
        SecretKey secretKey = new SecretKeySpec(secretBytes, ALGORITHM);
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey));
        this.expiration = expiration;
    }

    public String generateToken(String email, Role role) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(email).claim("role", role.name()).issuedAt(issuedAt).expiresAt(issuedAt.plus(expiration)).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return expiration.toSeconds();
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
