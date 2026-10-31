package com.agendastyle.backend.staff;

import com.agendastyle.backend.catalog.SalonService;
import com.agendastyle.backend.catalog.SalonServiceRepository;
import com.agendastyle.backend.staff.dto.CreateEmployeeRequest;
import com.agendastyle.backend.staff.dto.EmployeeResponse;
import com.agendastyle.backend.staff.exception.EmployeeEmailAlreadyExistsException;
import com.agendastyle.backend.staff.exception.InvalidServiceSelectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Mock
    private SalonService secondSalonService;

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
        assertEquals("Eva", response.firstName());
        assertEquals("Sanz", response.lastName());
        assertEquals("600000001", response.phone());
        assertTrue(response.active());
        assertEquals(Set.of(1L), response.serviceIds());
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

    @Test
    void shouldCreateEmployeeWithMultipleServices() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example.com", Set.of(1L, 2L));
        when(salonServiceRepository.findAllById(request.serviceIds()))
                .thenReturn(List.of(salonService, secondSalonService));
        when(salonService.getId()).thenReturn(1L);
        when(secondSalonService.getId()).thenReturn(2L);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeApplicationService.create(request);

        assertEquals(Set.of(1L, 2L), response.serviceIds());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldRejectRequestWhenAnySelectedServiceDoesNotExist() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example.com", Set.of(1L, 999L));
        when(salonServiceRepository.findAllById(request.serviceIds())).thenReturn(List.of(salonService));

        InvalidServiceSelectionException exception = assertThrows(
                InvalidServiceSelectionException.class,
                () -> employeeApplicationService.create(request));

        assertEquals("One or more selected services do not exist", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void shouldReturnAllEmployeesAsResponses() {
        when(salonService.getId()).thenReturn(1L);
        when(secondSalonService.getId()).thenReturn(2L);
        Employee firstEmployee = new Employee("Eva", "Sanz", "600000001", "eva@example.com");
        firstEmployee.assignServices(Set.of(salonService));
        Employee secondEmployee = new Employee("Luis", "Ruiz", "600000002", "luis@example.com");
        secondEmployee.assignServices(Set.of(secondSalonService));
        when(employeeRepository.findAll()).thenReturn(List.of(firstEmployee, secondEmployee));

        List<EmployeeResponse> responses = employeeApplicationService.findAll();

        assertEquals(2, responses.size());
        assertEquals("Eva", responses.get(0).firstName());
        assertEquals(Set.of(1L), responses.get(0).serviceIds());
        assertEquals("Luis", responses.get(1).firstName());
        assertEquals(Set.of(2L), responses.get(1).serviceIds());
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        assertTrue(employeeApplicationService.findAll().isEmpty());
    }
}
