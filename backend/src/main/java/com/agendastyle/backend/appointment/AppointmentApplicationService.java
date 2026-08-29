package com.agendastyle.backend.appointment;

import com.agendastyle.backend.appointment.dto.AppointmentResponse;
import com.agendastyle.backend.appointment.dto.CreateAppointmentRequest;
import com.agendastyle.backend.appointment.exception.AppointmentResourceNotFoundException;
import com.agendastyle.backend.appointment.exception.InvalidAppointmentException;
import com.agendastyle.backend.appointment.exception.AppointmentOverlapException;
import com.agendastyle.backend.catalog.SalonService;
import com.agendastyle.backend.catalog.SalonServiceRepository;
import com.agendastyle.backend.client.Client;
import com.agendastyle.backend.client.ClientRepository;
import com.agendastyle.backend.staff.Employee;
import com.agendastyle.backend.staff.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentApplicationService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final SalonServiceRepository salonServiceRepository;

    public AppointmentApplicationService(AppointmentRepository appointmentRepository, ClientRepository clientRepository, EmployeeRepository employeeRepository, SalonServiceRepository salonServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.salonServiceRepository = salonServiceRepository;
    }

    @Transactional
    public AppointmentResponse create(CreateAppointmentRequest request) {

        Client client = clientRepository.findById(request.clientId()).orElseThrow(() -> new AppointmentResourceNotFoundException("Client not found"));

        Employee employee = employeeRepository.findById(request.employeeId()).orElseThrow(() -> new AppointmentResourceNotFoundException("Employee not found"));

        SalonService service = salonServiceRepository.findById(request.serviceId()).orElseThrow(() -> new AppointmentResourceNotFoundException("Service not found"));

        if (!employee.isActive()) {
            throw new InvalidAppointmentException("The selected employee is not active");
        }

        if (!service.isActive()) {
            throw new InvalidAppointmentException("The selected service is not active");
        }

        if (!employeeCanProvideService(employee, service)) {
            throw new InvalidAppointmentException("The selected employee cannot provide this service");
        }

        if (request.startDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException(
                    "The appointment cannot be in the past"
            );
        }

        LocalDateTime endDateTime = request.startDateTime().plusMinutes(service.getDurationMinutes());

        boolean overlapExists = appointmentRepository.existsOverlappingAppointment(employee.getId(), request.startDateTime(), endDateTime, AppointmentStatus.CANCELLED);
        if (overlapExists) {
            throw new AppointmentOverlapException();
        }

        Appointment appointment = new Appointment(
                request.startDateTime(),
                endDateTime,
                request.notes(),
                client,
                employee,
                service
        );

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return toResponse(savedAppointment);
    }
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {

        List<Appointment> appointments = appointmentRepository.findAll();
        List<AppointmentResponse> responses = new ArrayList<>();

        for (Appointment appointment : appointments) {
            responses.add(toResponse(appointment));
        }

        return responses;
    }

    private boolean employeeCanProvideService(Employee employee, SalonService selectedService) {
        for (SalonService service : employee.getServices()) {
            if (service.getId().equals(selectedService.getId())) {
                return true;
            }
        }
        return false;
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStartDateTime(),
                appointment.getEndDateTime(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getClient().getId(),
                appointment.getEmployee().getId(),
                appointment.getService().getId()
        );
    }
}