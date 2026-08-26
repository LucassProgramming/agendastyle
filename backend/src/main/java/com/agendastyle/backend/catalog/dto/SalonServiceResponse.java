package com.agendastyle.backend.catalog.dto;

import java.math.BigDecimal;

public record SalonServiceResponse(Long id,
                                   String name,
                                   String description,
                                   Integer durationMinutes,
                                   BigDecimal price,
                                   boolean active){}