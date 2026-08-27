package com.agendastyle.backend.staff.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotBlank
        @Size(max = 30)
        String phone,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotEmpty
        Set<Long> serviceIds
) {
}