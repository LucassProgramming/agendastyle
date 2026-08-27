package com.agendastyle.backend.staff.exception;

public class EmployeeEmailAlreadyExistsException extends RuntimeException {

    public EmployeeEmailAlreadyExistsException(String email) {
        super("An employee with email " + email + " already exists");
    }
}