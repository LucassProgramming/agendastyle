package com.agendastyle.backend.client;

import com.agendastyle.backend.client.dto.ClientResponse;
import com.agendastyle.backend.client.dto.CreateClientRequest;
import com.agendastyle.backend.client.exception.ClientEmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
