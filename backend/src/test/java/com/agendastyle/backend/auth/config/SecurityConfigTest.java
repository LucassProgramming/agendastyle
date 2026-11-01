package com.agendastyle.backend.auth.config;

import com.agendastyle.backend.auth.AuthApplicationService;
import com.agendastyle.backend.auth.AuthController;
import com.agendastyle.backend.auth.Role;
import com.agendastyle.backend.auth.dto.LoginResponse;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(controllers = {SalonServiceController.class, ClientController.class, AuthController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalonServiceApplicationService salonServiceApplicationService;

    @MockitoBean
    private ClientApplicationService clientApplicationService;

    @MockitoBean
    private AuthApplicationService authApplicationService;

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
    @WithMockUser(username = "ana@example.com", roles = "CLIENTE")
    void shouldAllowProtectedEndpointForAuthenticatedUser() throws Exception {
        when(clientApplicationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clients")).andExpect(status().isOk());

        verify(clientApplicationService).findAll();
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
