package com.agendastyle.backend.staff;

import com.agendastyle.backend.appointment.AppointmentApplicationService;
import com.agendastyle.backend.appointment.dto.AppointmentResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeAgendaController {

    private final AppointmentApplicationService appointmentApplicationService;

    public EmployeeAgendaController(AppointmentApplicationService appointmentApplicationService) {
        this.appointmentApplicationService = appointmentApplicationService;
    }

    @GetMapping("/{employeeId}/appointments")
    public ResponseEntity<List<AppointmentResponse>> findDailyAgenda(@PathVariable Long employeeId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentApplicationService.findDailyAgenda(employeeId, date));
    }
}