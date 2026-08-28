package com.agendastyle.backend.appointment.dto;

import com.agendastyle.backend.appointment.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        AppointmentStatus status,
        String notes,
        LocalDateTime createdAt,
        Long clientId,
        Long employeeId,
        Long serviceId
) {
}