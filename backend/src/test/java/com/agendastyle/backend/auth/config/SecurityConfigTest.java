package com.agendastyle.backend.auth.config;

import com.agendastyle.backend.catalog.SalonServiceApplicationService;
import com.agendastyle.backend.catalog.SalonServiceController;
import com.agendastyle.backend.client.ClientApplicationService;
import com.agendastyle.backend.client.ClientController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SalonServiceController.class, ClientController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalonServiceApplicationService salonServiceApplicationService;

    @MockitoBean
    private ClientApplicationService clientApplicationService;

    @Test
    void shouldAllowPublicServiceCatalogWithoutAuthentication() throws Exception {
        when(salonServiceApplicationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk());

        verify(salonServiceApplicationService).findAll();
    }

    @Test
    void shouldRejectProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(clientApplicationService);
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "CLIENTE")
    void shouldAllowProtectedEndpointForAuthenticatedUser() throws Exception {
        when(clientApplicationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk());

        verify(clientApplicationService).findAll();
    }
}
