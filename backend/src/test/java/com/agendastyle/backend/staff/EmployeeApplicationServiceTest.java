package com.agendastyle.backend.staff;

import com.agendastyle.backend.catalog.SalonService;
import com.agendastyle.backend.catalog.SalonServiceRepository;
import com.agendastyle.backend.staff.dto.CreateEmployeeRequest;
import com.agendastyle.backend.staff.dto.EmployeeResponse;
import com.agendastyle.backend.staff.exception.EmployeeEmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeApplicationServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalonServiceRepository salonServiceRepository;

    @Mock
    private SalonService salonService;

    private EmployeeApplicationService employeeApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeApplicationService = new EmployeeApplicationService(
                employeeRepository, salonServiceRepository);
    }

    @Test
    void shouldNormalizeEmailBeforeCheckingAndSaving() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "  EVA@Example.COM  ", Set.of(1L));
        when(salonServiceRepository.findAllById(Set.of(1L))).thenReturn(List.of(salonService));
        when(salonService.getId()).thenReturn(1L);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeApplicationService.create(request);

        assertEquals("eva@example.com", response.email());
        verify(employeeRepository).existsByEmailIgnoreCase("eva@example.com");
    }

    @Test
    void shouldRejectEmailAlreadyUsedWithDifferentCase() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "EVA@Example.COM", Set.of(1L));
        when(employeeRepository.existsByEmailIgnoreCase("eva@example.com")).thenReturn(true);

        assertThrows(EmployeeEmailAlreadyExistsException.class,
                () -> employeeApplicationService.create(request));

        verify(employeeRepository, never()).save(any(Employee.class));
        verify(salonServiceRepository, never()).findAllById(any());
    }
}
