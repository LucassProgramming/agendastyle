package com.agendastyle.backend.client;

import com.agendastyle.backend.client.dto.ClientResponse;
import com.agendastyle.backend.client.dto.CreateClientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientControllerTest {

    @Mock
    private ClientApplicationService clientApplicationService;

    private ClientController clientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientController = new ClientController(clientApplicationService);
    }

    @Test
    void shouldReturnCreatedWhenClientIsCreated() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", "600000000", "ana@example.com");
        ClientResponse response = response(1L, "Ana", "ana@example.com");
        when(clientApplicationService.create(request)).thenReturn(response);

        ResponseEntity<ClientResponse> result = clientController.create(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(clientApplicationService).create(request);
    }

    @Test
    void shouldReturnOkWithAllClients() {
        List<ClientResponse> clients = List.of(
                response(1L, "Ana", "ana@example.com"),
                response(2L, "Luis", "luis@example.com"));
        when(clientApplicationService.findAll()).thenReturn(clients);

        ResponseEntity<List<ClientResponse>> result = clientController.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(clients, result.getBody());
        verify(clientApplicationService).findAll();
    }

    private static ClientResponse response(Long id, String firstName, String email) {
        return new ClientResponse(
                id, firstName, "Ruiz", "600000000", email, LocalDateTime.of(2026, 1, 1, 10, 0));
    }
}
