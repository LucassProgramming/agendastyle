package com.agendastyle.backend.client;

import com.agendastyle.backend.client.dto.ClientResponse;
import com.agendastyle.backend.client.dto.CreateClientRequest;
import com.agendastyle.backend.client.exception.ClientEmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientApplicationServiceTest {

    @Mock
    private ClientRepository clientRepository;

    private ClientApplicationService clientApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientApplicationService = new ClientApplicationService(clientRepository);
    }

    @Test
    void shouldNormalizeEmailBeforeCheckingAndSaving() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", "600000000", "  ANA@Example.COM  ");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse response = clientApplicationService.create(request);

        assertEquals("ana@example.com", response.email());
        assertEquals("Ana", response.firstName());
        assertEquals("Ruiz", response.lastName());
        assertEquals("600000000", response.phone());
        assertNotNull(response.registrationDate());
        verify(clientRepository).existsByEmailIgnoreCase("ana@example.com");
    }

    @Test
    void shouldRejectEmailAlreadyUsedWithDifferentCase() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", "600000000", "ANA@Example.COM");
        when(clientRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        assertThrows(ClientEmailAlreadyExistsException.class,
                () -> clientApplicationService.create(request));

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void shouldReturnAllClientsAsResponses() {
        Client firstClient = new Client("Ana", "Ruiz", "600000000", "ana@example.com");
        Client secondClient = new Client("Luis", "Sanz", "600000001", "luis@example.com");
        when(clientRepository.findAll()).thenReturn(List.of(firstClient, secondClient));

        List<ClientResponse> responses = clientApplicationService.findAll();

        assertEquals(2, responses.size());
        assertEquals("Ana", responses.get(0).firstName());
        assertEquals("ana@example.com", responses.get(0).email());
        assertEquals("Luis", responses.get(1).firstName());
        assertEquals("luis@example.com", responses.get(1).email());
        assertTrue(responses.stream().allMatch(response -> response.registrationDate() != null));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoClients() {
        when(clientRepository.findAll()).thenReturn(List.of());

        List<ClientResponse> responses = clientApplicationService.findAll();

        assertTrue(responses.isEmpty());
    }
}
