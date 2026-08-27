package com.agendastyle.backend.staff;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agendastyle.backend.catalog.SalonService;
import com.agendastyle.backend.catalog.SalonServiceRepository;
import com.agendastyle.backend.staff.dto.CreateEmployeeRequest;
import com.agendastyle.backend.staff.dto.EmployeeResponse;

import com.agendastyle.backend.staff.exception.EmployeeEmailAlreadyExistsException;
import com.agendastyle.backend.staff.exception.InvalidServiceSelectionException;

@Service
public class EmployeeApplicationService{
    private final EmployeeRepository employeeRepository;
    private final SalonServiceRepository salonServiceRepository;

    public EmployeeApplicationService(EmployeeRepository employeeRepository, SalonServiceRepository salonServiceRepository){
        this.employeeRepository = employeeRepository;
        this.salonServiceRepository = salonServiceRepository;
    }
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request){
        if(employeeRepository.existsByEmail(request.email())){
            throw new EmployeeEmailAlreadyExistsException(request.email());
        }
        Set<SalonService> services = new HashSet<>(salonServiceRepository.findAllById(request.serviceIds()));
        if (services.size() != request.serviceIds().size()) {
            throw new InvalidServiceSelectionException();
        }
        Employee employee = new Employee(request.firstName(), request.lastName(), request.phone(), request.email());
        employee.assignServices(services);
        Employee savedEmployee = employeeRepository.save(employee);

         Set<Long> serviceIds = savedEmployee.getServices()
                .stream()
                .map(SalonService::getId)
                .collect(Collectors.toSet());
        return new EmployeeResponse(savedEmployee.getId(), savedEmployee.getFirstName(),savedEmployee.getLastName(),savedEmployee.getPhone(),savedEmployee.getEmail(),savedEmployee.isActive(), serviceIds);
    }
    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {

        List<Employee> employees = employeeRepository.findAll();

        List<EmployeeResponse> responses = new ArrayList<>();

        for (Employee employee : employees) {

            Set<Long> serviceIds = new HashSet<>();

            for (SalonService service : employee.getServices()) {
                serviceIds.add(service.getId());
            }

            EmployeeResponse response = new EmployeeResponse(
                    employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getPhone(),
                    employee.getEmail(),
                    employee.isActive(),
                    serviceIds
        );
        responses.add(response);
    }
    return responses;
}

}