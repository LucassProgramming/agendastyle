package com.agendastyle.backend.staff;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
}