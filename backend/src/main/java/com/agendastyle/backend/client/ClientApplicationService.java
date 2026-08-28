package com.agendastyle.backend.client;

import com.agendastyle.backend.client.dto.ClientResponse;
import com.agendastyle.backend.client.dto.CreateClientRequest;
import com.agendastyle.backend.client.exception.ClientEmailAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientApplicationService {

    private final ClientRepository clientRepository;

    public ClientApplicationService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse create(CreateClientRequest request) {

        if (clientRepository.existsByEmail(request.email())) {
            throw new ClientEmailAlreadyExistsException(request.email());
        }

        Client client = new Client(
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.email()
        );

        Client savedClient = clientRepository.save(client);

        return new ClientResponse(
                savedClient.getId(),
                savedClient.getFirstName(),
                savedClient.getLastName(),
                savedClient.getPhone(),
                savedClient.getEmail(),
                savedClient.getRegistrationDate()
        );
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {

        List<Client> clients = clientRepository.findAll();
        List<ClientResponse> responses = new ArrayList<>();

        for (Client client : clients) {
            ClientResponse response = new ClientResponse(
                    client.getId(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPhone(),
                    client.getEmail(),
                    client.getRegistrationDate()
            );

            responses.add(response);
        }
        return responses;
    }
}