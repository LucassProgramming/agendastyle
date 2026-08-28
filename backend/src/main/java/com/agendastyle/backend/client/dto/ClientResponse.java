package com.agendastyle.backend.client.dto;

import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        LocalDateTime registrationDate
) {
}