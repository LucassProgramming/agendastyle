package com.agendastyle.backend.staff.exception;

public class InvalidEmployeeScheduleException extends RuntimeException {

    public InvalidEmployeeScheduleException(String message) {
        super(message);
    }
}