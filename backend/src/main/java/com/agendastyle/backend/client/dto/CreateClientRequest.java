package com.agendastyle.backend.client.dto;

import com.agendastyle.backend.common.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(

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
        String email

) {
}
