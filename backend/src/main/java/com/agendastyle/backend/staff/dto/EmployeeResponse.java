package com.agendastyle.backend.staff.dto;

import java.util.Set;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        boolean active,
        Set<Long> serviceIds
) {
}