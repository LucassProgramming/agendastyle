package com.agendastyle.backend.staff.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long employeeId) {
        super("Employee with id " + employeeId + " not found");
    }
}