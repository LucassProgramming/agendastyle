package com.agendastyle.backend.staff;

import com.agendastyle.backend.staff.dto.schedule.CreateEmployeeScheduleRequest;
import com.agendastyle.backend.staff.dto.schedule.EmployeeScheduleResponse;
import com.agendastyle.backend.staff.exception.EmployeeNotFoundException;
import com.agendastyle.backend.staff.exception.InvalidEmployeeScheduleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeScheduleApplicationService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;

    public EmployeeScheduleApplicationService(EmployeeRepository employeeRepository, EmployeeScheduleRepository employeeScheduleRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
    }

    @Transactional
    public EmployeeScheduleResponse create(Long employeeId, CreateEmployeeScheduleRequest request) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (!request.startTime().isBefore(request.endTime())) {
            throw new InvalidEmployeeScheduleException("Start time must be before end time");
        }

        EmployeeSchedule schedule = new EmployeeSchedule(employee, request.dayOfWeek(), request.startTime(), request.endTime());

        EmployeeSchedule savedSchedule = employeeScheduleRepository.save(schedule);

        return toResponse(savedSchedule);
    }

    @Transactional(readOnly = true)
    public List<EmployeeScheduleResponse> findByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }

        List<EmployeeSchedule> schedules = employeeScheduleRepository.findByEmployeeId(employeeId);
        List<EmployeeScheduleResponse> responses = new ArrayList<>();

        for (EmployeeSchedule schedule : schedules) {
            responses.add(toResponse(schedule));
        }

        return responses;
    }

    private EmployeeScheduleResponse toResponse(EmployeeSchedule schedule) {
        return new EmployeeScheduleResponse(
                schedule.getId(),
                schedule.getEmployee().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
    }
}