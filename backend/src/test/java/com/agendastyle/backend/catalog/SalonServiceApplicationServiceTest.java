package com.agendastyle.backend.catalog;

import com.agendastyle.backend.catalog.dto.CreateSalonServiceRequest;
import com.agendastyle.backend.catalog.dto.SalonServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SalonServiceApplicationServiceTest {

    @Mock
    private SalonServiceRepository salonServiceRepository;

    @Mock
    private SalonService persistedService;

    private SalonServiceApplicationService salonServiceApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        salonServiceApplicationService = new SalonServiceApplicationService(salonServiceRepository);
    }

    @Test
    void shouldCreateServiceAndReturnPersistedData() {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "Corte", "Corte y peinado", 45, new BigDecimal("25.50"));
        when(salonServiceRepository.save(any(SalonService.class))).thenReturn(persistedService);
        when(persistedService.getId()).thenReturn(7L);
        when(persistedService.getName()).thenReturn("Corte");
        when(persistedService.getDescription()).thenReturn("Corte y peinado");
        when(persistedService.getDurationMinutes()).thenReturn(45);
        when(persistedService.getPrice()).thenReturn(new BigDecimal("25.50"));
        when(persistedService.isActive()).thenReturn(true);

        SalonServiceResponse response = salonServiceApplicationService.create(request);

        assertEquals(7L, response.id());
        assertEquals("Corte", response.name());
        assertEquals("Corte y peinado", response.description());
        assertEquals(45, response.durationMinutes());
        assertEquals(new BigDecimal("25.50"), response.price());
        assertTrue(response.active());

        ArgumentCaptor<SalonService> captor = ArgumentCaptor.forClass(SalonService.class);
        verify(salonServiceRepository).save(captor.capture());
        assertEquals("Corte", captor.getValue().getName());
        assertEquals(45, captor.getValue().getDurationMinutes());
    }

    @Test
    void shouldReturnAllServicesAsResponses() {
        when(persistedService.getId()).thenReturn(3L);
        when(persistedService.getName()).thenReturn("Color");
        when(persistedService.getDescription()).thenReturn("Color completo");
        when(persistedService.getDurationMinutes()).thenReturn(90);
        when(persistedService.getPrice()).thenReturn(new BigDecimal("49.90"));
        when(persistedService.isActive()).thenReturn(true);
        when(salonServiceRepository.findAll()).thenReturn(List.of(persistedService));

        List<SalonServiceResponse> responses = salonServiceApplicationService.findAll();

        assertEquals(1, responses.size());
        assertEquals("Color", responses.getFirst().name());
        assertEquals(new BigDecimal("49.90"), responses.getFirst().price());
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoServices() {
        when(salonServiceRepository.findAll()).thenReturn(List.of());

        assertTrue(salonServiceApplicationService.findAll().isEmpty());
    }
}
