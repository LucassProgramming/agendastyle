package com.agendastyle.backend.staff;

import com.agendastyle.backend.staff.dto.schedule.CreateEmployeeScheduleRequest;
import com.agendastyle.backend.staff.dto.schedule.EmployeeScheduleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/schedules")
public class EmployeeScheduleController {

    private final EmployeeScheduleApplicationService employeeScheduleApplicationService;

    public EmployeeScheduleController(EmployeeScheduleApplicationService employeeScheduleApplicationService) {
        this.employeeScheduleApplicationService = employeeScheduleApplicationService;
    }

    @PostMapping
    public ResponseEntity<EmployeeScheduleResponse> create(@PathVariable Long employeeId, @Valid @RequestBody CreateEmployeeScheduleRequest request) {
        EmployeeScheduleResponse response = employeeScheduleApplicationService.create(employeeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeScheduleResponse>> findByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeScheduleApplicationService.findByEmployee(employeeId));
    }
}