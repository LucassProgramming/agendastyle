package com.agendastyle.backend.staff.dto;

import java.util.Set;

import com.agendastyle.backend.common.validation.ValidEmail;
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
        @ValidEmail
        String email,

        @NotEmpty
        Set<Long> serviceIds
) {
}
