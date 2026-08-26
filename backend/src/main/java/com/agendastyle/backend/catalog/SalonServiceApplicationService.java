package com.agendastyle.backend.catalog;
import com.agendastyle.backend.catalog.dto.CreateSalonServiceRequest;
import com.agendastyle.backend.catalog.dto.SalonServiceResponse;

import org.springframework.stereotype.Service;
import java.util.List;



@Service
public class SalonServiceApplicationService {

    private final SalonServiceRepository salonServiceRepository;

    public SalonServiceApplicationService(SalonServiceRepository salonServiceRepository) {
        this.salonServiceRepository = salonServiceRepository;
    }

    public SalonServiceResponse create(CreateSalonServiceRequest request){
        SalonService salonService = new SalonService(request.name(), request.description(), request.durationMinutes(), request.price());
        SalonService savedService = salonServiceRepository.save(salonService);
        return new SalonServiceResponse(savedService.getId(), savedService.getName(), savedService.getDescription(), savedService.getDurationMinutes(), savedService.getPrice(), savedService.isActive());
    }
    public List<SalonServiceResponse> findAll(){
        return salonServiceRepository.findAll().stream().map(service -> new SalonServiceResponse(
                                                               service.getId(),
                                                               service.getName(),
                                                               service.getDescription(),
                                                               service.getDurationMinutes(),
                                                               service.getPrice(),
                                                               service.isActive()     
        )).toList();
    }
}