package com.agendastyle.backend.auth.config;

import com.agendastyle.backend.auth.AuthApplicationService;
import com.agendastyle.backend.auth.AuthController;
import com.agendastyle.backend.auth.Role;
import com.agendastyle.backend.auth.dto.LoginResponse;
import com.agendastyle.backend.appointment.AppointmentApplicationService;
import com.agendastyle.backend.appointment.ClientAppointmentController;
import com.agendastyle.backend.auth.jwt.JwtAuthenticationEntryPoint;
import com.agendastyle.backend.auth.jwt.JwtAuthenticationFilter;
import com.agendastyle.backend.auth.jwt.JwtAccessDeniedHandler;
import com.agendastyle.backend.auth.jwt.JwtService;
import com.agendastyle.backend.catalog.SalonServiceApplicationService;
import com.agendastyle.backend.catalog.SalonServiceController;
import com.agendastyle.backend.client.ClientApplicationService;
import com.agendastyle.backend.client.ClientController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(controllers = {SalonServiceController.class, ClientController.class, AuthController.class, ClientAppointmentController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalonServiceApplicationService salonServiceApplicationService;

    @MockitoBean
    private ClientApplicationService clientApplicationService;

    @MockitoBean
    private AuthApplicationService authApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppointmentApplicationService appointmentApplicationService;

    @Test
    void shouldAllowPublicServiceCatalogWithoutAuthentication() throws Exception {
        when(salonServiceApplicationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/services")).andExpect(status().isOk());

        verify(salonServiceApplicationService).findAll();
    }

    @Test
    void shouldRejectProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/clients")).andExpect(status().isUnauthorized());

        verifyNoInteractions(clientApplicationService);
    }

    @Test
    @WithMockUser(username = "admin@agendastyle.com", roles = "ADMIN")
    void shouldAllowProtectedEndpointForAuthenticatedUser() throws Exception {
        when(clientApplicationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clients")).andExpect(status().isOk());

        verify(clientApplicationService).findAll();
    }

    @Test
    void shouldAuthenticateValidBearerToken() throws Exception {
        Instant issuedAt = Instant.now();
        Jwt jwt = Jwt.withTokenValue("valid.jwt.token").header("alg", "HS256").subject("admin@agendastyle.com").claim("role", "ADMIN").issuedAt(issuedAt).expiresAt(issuedAt.plusSeconds(3600)).build();
        when(jwtService.parseToken("valid.jwt.token")).thenReturn(jwt);
        AtomicReference<Authentication> capturedAuthentication = new AtomicReference<>();
        when(clientApplicationService.findAll()).thenAnswer(invocation -> {
            capturedAuthentication.set(SecurityContextHolder.getContext().getAuthentication());
            return List.of();
        });

        mockMvc.perform(get("/api/v1/clients").header("Authorization", "Bearer valid.jwt.token")).andExpect(status().isOk());

        assertEquals("admin@agendastyle.com", capturedAuthentication.get().getName());
        assertEquals("ROLE_ADMIN", capturedAuthentication.get().getAuthorities().iterator().next().getAuthority());
        verify(clientApplicationService).findAll();
    }

    @Test
    void shouldReturnForbiddenWhenAuthenticatedUserLacksAdminRole() throws Exception {
        Instant issuedAt = Instant.now();
        Jwt jwt = Jwt.withTokenValue("client.jwt.token").header("alg", "HS256").subject("ana@example.com").claim("role", "CLIENTE").issuedAt(issuedAt).expiresAt(issuedAt.plusSeconds(3600)).build();
        when(jwtService.parseToken("client.jwt.token")).thenReturn(jwt);

        mockMvc.perform(get("/api/v1/clients").header("Authorization", "Bearer client.jwt.token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));

        verifyNoInteractions(clientApplicationService);
    }

    @Test
    void shouldAllowClientToAccessOwnAppointments() throws Exception {
        Instant issuedAt = Instant.now();
        Jwt jwt = Jwt.withTokenValue("client.jwt.token").header("alg", "HS256").subject("ana@example.com").claim("role", "CLIENTE").issuedAt(issuedAt).expiresAt(issuedAt.plusSeconds(3600)).build();
        when(jwtService.parseToken("client.jwt.token")).thenReturn(jwt);
        when(appointmentApplicationService.findAppointmentsForClient("ana@example.com")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/me/appointments").header("Authorization", "Bearer client.jwt.token")).andExpect(status().isOk());

        verify(appointmentApplicationService).findAppointmentsForClient("ana@example.com");
    }

    @Test
    void shouldRejectEmployeeFromClientAppointments() throws Exception {
        Instant issuedAt = Instant.now();
        Jwt jwt = Jwt.withTokenValue("employee.jwt.token").header("alg", "HS256").subject("employee@agendastyle.com").claim("role", "EMPLEADO").issuedAt(issuedAt).expiresAt(issuedAt.plusSeconds(3600)).build();
        when(jwtService.parseToken("employee.jwt.token")).thenReturn(jwt);

        mockMvc.perform(get("/api/v1/me/appointments").header("Authorization", "Bearer employee.jwt.token")).andExpect(status().isForbidden());

        verifyNoInteractions(appointmentApplicationService);
    }

    @Test
    void shouldRejectInvalidBearerToken() throws Exception {
        when(jwtService.parseToken("invalid.jwt.token")).thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(get("/api/v1/clients").header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required"));

        verifyNoInteractions(clientApplicationService);
    }

    @Test
    void shouldRejectEmptyBearerToken() throws Exception {
        mockMvc.perform(get("/api/v1/clients").header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(clientApplicationService);
    }

    @Test
    void shouldAllowLoginWithoutPreviousAuthentication() throws Exception {
        when(authApplicationService.login(org.mockito.ArgumentMatchers.any())).thenReturn(new LoginResponse("signed.jwt.token", "Bearer", 3600L, "ana@example.com", Role.CLIENTE));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ana@example.com",
                                  "password": "correct-password"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidLoginCredentials() throws Exception {
        when(authApplicationService.login(org.mockito.ArgumentMatchers.any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ana@example.com",
                                  "password": "incorrect"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
