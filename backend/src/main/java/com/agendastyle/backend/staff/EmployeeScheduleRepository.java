package com.agendastyle.backend.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, Long> {

    List<EmployeeSchedule> findByEmployeeId(Long employeeId);

    List<EmployeeSchedule> findByEmployeeIdAndDayOfWeek(Long employeeId, DayOfWeek dayOfWeek);
}