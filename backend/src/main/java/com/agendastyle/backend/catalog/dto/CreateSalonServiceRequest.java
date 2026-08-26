package com.agendastyle.backend.catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSalonServiceRequest(
    @NotBlank 
    @Size(max = 100)
    String name,

    @Size(max = 500)
    String description,

    @NotNull 
    @Positive
    Integer durationMinutes,

    @NotNull 
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal price
){}