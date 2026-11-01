package com.agendastyle.backend.auth;

import com.agendastyle.backend.auth.dto.LoginRequest;
import com.agendastyle.backend.auth.dto.LoginResponse;
import com.agendastyle.backend.auth.jwt.JwtService;
import com.agendastyle.backend.common.validation.EmailAddress;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthApplicationService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = EmailAddress.normalize(request.email());

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(normalizedEmail,request.password());

        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

        Role role = extractRole(authenticationResult);
        String token = jwtService.generateToken(authenticationResult.getName(), role);
        return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds(), authenticationResult.getName(), role);
    }

    private Role extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Authenticated account has no role"));
    }
}
