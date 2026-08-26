package com.agendastyle.backend.catalog;

import com.agendastyle.backend.catalog.dto.CreateSalonServiceRequest;
import com.agendastyle.backend.catalog.dto.SalonServiceResponse;

import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;  

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("api/v1/services")
public class SalonServiceController{
    private final SalonServiceApplicationService salonServiceApplicationService;
    public SalonServiceController(SalonServiceApplicationService salonServiceApplicationService){
        this.salonServiceApplicationService = salonServiceApplicationService;
    }

    @PostMapping
    public ResponseEntity<SalonServiceResponse> create(@Valid @RequestBody CreateSalonServiceRequest request){
        SalonServiceResponse response =  salonServiceApplicationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<SalonServiceResponse>> findAll() {
        return ResponseEntity.ok(salonServiceApplicationService.findAll());
}
}