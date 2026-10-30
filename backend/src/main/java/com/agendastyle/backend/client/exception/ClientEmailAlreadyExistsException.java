package com.agendastyle.backend.client.exception;

public class ClientEmailAlreadyExistsException extends RuntimeException {

    public ClientEmailAlreadyExistsException(String email) {
        super("A client with email " + email + " already exists");
    }
}