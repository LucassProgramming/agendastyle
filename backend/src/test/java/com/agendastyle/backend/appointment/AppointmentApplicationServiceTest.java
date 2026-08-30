package com.agendastyle.backend.appointment;

import com.agendastyle.backend.appointment.dto.AppointmentResponse;
import com.agendastyle.backend.appointment.dto.CreateAppointmentRequest;
import com.agendastyle.backend.appointment.exception.AppointmentOutsideWorkingHoursException;
import com.agendastyle.backend.appointment.exception.AppointmentOverlapException;
import com.agendastyle.backend.appointment.exception.AppointmentResourceNotFoundException;
import com.agendastyle.backend.appointment.exception.InvalidAppointmentException;
import com.agendastyle.backend.catalog.SalonService;
import com.agendastyle.backend.catalog.SalonServiceRepository;
import com.agendastyle.backend.client.Client;
import com.agendastyle.backend.client.ClientRepository;
import com.agendastyle.backend.staff.Employee;
import com.agendastyle.backend.staff.EmployeeRepository;
import com.agendastyle.backend.staff.EmployeeSchedule;
import com.agendastyle.backend.staff.EmployeeScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentApplicationServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalonServiceRepository salonServiceRepository;

    @Mock
    private EmployeeScheduleRepository employeeScheduleRepository;

    @Mock
    private Client client;

    @Mock
    private Employee employee;

    @Mock
    private SalonService service;

    @Mock
    private EmployeeSchedule employeeSchedule;

    private AppointmentApplicationService appointmentApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        appointmentApplicationService = new AppointmentApplicationService(appointmentRepository, clientRepository, employeeRepository, salonServiceRepository, employeeScheduleRepository);
    }

    @Test
    void shouldCalculateEndDateTimeUsingServiceDuration() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(10, 0);
        LocalDateTime endDateTime = startDateTime.plusMinutes(30);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, "First appointment");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(client.getId()).thenReturn(1L);

        when(employee.getId()).thenReturn(1L);
        when(employee.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of(service));

        when(service.getId()).thenReturn(2L);
        when(service.isActive()).thenReturn(true);
        when(service.getDurationMinutes()).thenReturn(30);

        when(employeeScheduleRepository.findByEmployeeIdAndDayOfWeek(1L, startDateTime.getDayOfWeek())).thenReturn(List.of(employeeSchedule));
        when(employeeSchedule.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(employeeSchedule.getEndTime()).thenReturn(LocalTime.of(18, 0));

        when(appointmentRepository.existsOverlappingAppointment(1L, startDateTime, endDateTime, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentApplicationService.create(request);

        assertEquals(endDateTime, response.endDateTime());
    }

    @Test
    void shouldRejectAppointmentWhenEmployeeCannotProvideService() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(11, 0);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(employee.isActive()).thenReturn(true);
        when(service.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of());

        InvalidAppointmentException exception = assertThrows(InvalidAppointmentException.class, () -> appointmentApplicationService.create(request));

        assertEquals("The selected employee cannot provide this service", exception.getMessage());
    }

    @Test
    void shouldRejectAppointmentInThePast() {
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(employee.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of(service));

        when(service.getId()).thenReturn(2L);
        when(service.isActive()).thenReturn(true);

        InvalidAppointmentException exception = assertThrows(InvalidAppointmentException.class, () -> appointmentApplicationService.create(request));

        assertEquals("The appointment cannot be in the past", exception.getMessage());
    }

    @Test
    void shouldRejectAppointmentWhenClientDoesNotExist() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(12, 0);

        CreateAppointmentRequest request = new CreateAppointmentRequest(999L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        AppointmentResourceNotFoundException exception = assertThrows(AppointmentResourceNotFoundException.class, () -> appointmentApplicationService.create(request));

        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void shouldRejectOverlappingAppointment() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(10, 15);
        LocalDateTime endDateTime = startDateTime.plusMinutes(30);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(employee.getId()).thenReturn(1L);
        when(employee.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of(service));

        when(service.getId()).thenReturn(2L);
        when(service.isActive()).thenReturn(true);
        when(service.getDurationMinutes()).thenReturn(30);

        when(employeeScheduleRepository.findByEmployeeIdAndDayOfWeek(1L, startDateTime.getDayOfWeek())).thenReturn(List.of(employeeSchedule));
        when(employeeSchedule.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(employeeSchedule.getEndTime()).thenReturn(LocalTime.of(18, 0));

        when(appointmentRepository.existsOverlappingAppointment(1L, startDateTime, endDateTime, AppointmentStatus.CANCELLED)).thenReturn(true);

        AppointmentOverlapException exception = assertThrows(AppointmentOverlapException.class, () -> appointmentApplicationService.create(request));

        assertEquals("The selected employee already has an appointment during this time", exception.getMessage());

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void shouldAllowAppointmentStartingWhenPreviousAppointmentEnds() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(10, 30);
        LocalDateTime endDateTime = startDateTime.plusMinutes(30);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(client.getId()).thenReturn(1L);

        when(employee.getId()).thenReturn(1L);
        when(employee.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of(service));

        when(service.getId()).thenReturn(2L);
        when(service.isActive()).thenReturn(true);
        when(service.getDurationMinutes()).thenReturn(30);

        when(employeeScheduleRepository.findByEmployeeIdAndDayOfWeek(1L, startDateTime.getDayOfWeek())).thenReturn(List.of(employeeSchedule));
        when(employeeSchedule.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(employeeSchedule.getEndTime()).thenReturn(LocalTime.of(18, 0));

        when(appointmentRepository.existsOverlappingAppointment(1L, startDateTime, endDateTime, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentApplicationService.create(request);

        assertEquals(endDateTime, response.endDateTime());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void shouldRejectAppointmentOutsideWorkingHours() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(8, 30);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(employee.getId()).thenReturn(1L);
        when(employee.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of(service));

        when(service.getId()).thenReturn(2L);
        when(service.isActive()).thenReturn(true);
        when(service.getDurationMinutes()).thenReturn(30);

        when(employeeScheduleRepository.findByEmployeeIdAndDayOfWeek(1L, startDateTime.getDayOfWeek())).thenReturn(List.of(employeeSchedule));
        when(employeeSchedule.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(employeeSchedule.getEndTime()).thenReturn(LocalTime.of(18, 0));

        AppointmentOutsideWorkingHoursException exception = assertThrows(AppointmentOutsideWorkingHoursException.class, () -> appointmentApplicationService.create(request));

        assertEquals("The appointment is outside the employee working hours", exception.getMessage());

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void shouldAllowAppointmentInsideWorkingHours() {
        LocalDateTime startDateTime = LocalDate.now().plusDays(1).atTime(10, 0);
        LocalDateTime endDateTime = startDateTime.plusMinutes(30);

        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 2L, startDateTime, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salonServiceRepository.findById(2L)).thenReturn(Optional.of(service));

        when(client.getId()).thenReturn(1L);

        when(employee.getId()).thenReturn(1L);
        when(employee.isActive()).thenReturn(true);
        when(employee.getServices()).thenReturn(Set.of(service));

        when(service.getId()).thenReturn(2L);
        when(service.isActive()).thenReturn(true);
        when(service.getDurationMinutes()).thenReturn(30);

        when(employeeScheduleRepository.findByEmployeeIdAndDayOfWeek(1L, startDateTime.getDayOfWeek())).thenReturn(List.of(employeeSchedule));
        when(employeeSchedule.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(employeeSchedule.getEndTime()).thenReturn(LocalTime.of(18, 0));

        when(appointmentRepository.existsOverlappingAppointment(1L, startDateTime, endDateTime, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentApplicationService.create(request);

        assertEquals(endDateTime, response.endDateTime());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}