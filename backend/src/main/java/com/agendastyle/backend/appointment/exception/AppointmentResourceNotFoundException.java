package com.agendastyle.backend.appointment.exception;

public class AppointmentResourceNotFoundException extends RuntimeException {

    public AppointmentResourceNotFoundException(String message) {
        super(message);
    }
}