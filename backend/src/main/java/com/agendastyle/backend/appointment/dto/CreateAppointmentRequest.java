package com.agendastyle.backend.appointment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull
        Long clientId,

        @NotNull
        Long employeeId,

        @NotNull
        Long serviceId,

        @NotNull
        LocalDateTime startDateTime,

        @Size(max = 500)
        String notes
) {
}