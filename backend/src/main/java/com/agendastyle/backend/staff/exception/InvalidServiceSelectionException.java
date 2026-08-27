package com.agendastyle.backend.staff.exception;

public class InvalidServiceSelectionException extends RuntimeException {

    public InvalidServiceSelectionException() {
        super("One or more selected services do not exist");
    }
}