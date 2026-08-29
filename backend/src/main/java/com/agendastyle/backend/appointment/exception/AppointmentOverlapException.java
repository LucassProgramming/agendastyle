package com.agendastyle.backend.appointment.exception;

public class AppointmentOverlapException extends RuntimeException {

    public AppointmentOverlapException() {
        super("The selected employee already has an appointment during this time");
    }
}