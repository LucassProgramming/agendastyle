package com.agendastyle.backend.staff.dto.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record EmployeeScheduleResponse(
        Long id,
        Long employeeId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}