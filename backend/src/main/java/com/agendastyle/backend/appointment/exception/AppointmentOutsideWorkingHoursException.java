package com.agendastyle.backend.appointment.exception;

public class AppointmentOutsideWorkingHoursException extends RuntimeException {

    public AppointmentOutsideWorkingHoursException() {
        super("The appointment is outside the employee working hours");
    }
}