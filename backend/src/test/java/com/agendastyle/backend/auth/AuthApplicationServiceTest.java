package com.agendastyle.backend.auth;

import com.agendastyle.backend.auth.dto.LoginRequest;
import com.agendastyle.backend.auth.dto.LoginResponse;
import com.agendastyle.backend.auth.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthApplicationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authApplicationService = new AuthApplicationService(authenticationManager, jwtService);
    }

    @Test
    void shouldAuthenticateNormalizedCredentialsAndReturnIdentity() {
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated("ana@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated);
        when(jwtService.generateToken("ana@example.com", Role.CLIENTE)).thenReturn("signed.jwt.token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authApplicationService.login(new LoginRequest("  ANA@Example.COM  ", "correct-password"));

        assertEquals("ana@example.com", response.email());
        assertEquals(Role.CLIENTE, response.role());
        assertEquals("signed.jwt.token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("ana@example.com", captor.getValue().getName());
        assertEquals("correct-password", captor.getValue().getCredentials());
        verify(jwtService).generateToken("ana@example.com", Role.CLIENTE);
    }

    @Test
    void shouldPropagateBadCredentialsWithoutTryingToValidateThemManually() {
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authApplicationService.login(new LoginRequest("ana@example.com", "incorrect")));
    }
}
