package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.repository.SpotRepository;
import com.modasby.gestaoestacionamentos.web.dto.spotStatus.SpotStatusRequestDTO;
import com.modasby.gestaoestacionamentos.web.dto.spotStatus.SpotStatusResponseDTO;
import com.modasby.gestaoestacionamentos.web.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpotServiceTest {

    @Mock
    SpotRepository spotRepository;

    @Mock
    ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    SpotService spotService;

    private static final String LICENSE_PLATE = "ZUL0001";

    @Test
    void getSpotStatus_whenSpotNotFound_shouldThrowEntityNotFoundException() {
        final var requestDTO = new SpotStatusRequestDTO(-23.561684, -46.655981);

        when(spotRepository.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(null);

        var exception = assertThrows(EntityNotFoundException.class, () -> {
            spotService.getSpotStatus(requestDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Spot not found");

        verify(parkingSessionRepository, never()).findFirstBySpotAndExitTimeIsNotNull(any(Spot.class));
    }

    @Test
    void getSpotStatus_whenSpotIsFree_shouldReturnStatusAsFree() {
        final var requestDTO = new SpotStatusRequestDTO(-23.561684, -46.655981);
        final var mockSpot = new Spot(1, null, requestDTO.lat(), requestDTO.lng(), false);

        when(spotRepository.findByLatAndLng(requestDTO.lat(), requestDTO.lng())).thenReturn(mockSpot);
        when(parkingSessionRepository.findFirstBySpotAndExitTimeIsNotNull(mockSpot)).thenReturn(null); // Nenhuma sessão encontrada

        SpotStatusResponseDTO response = spotService.getSpotStatus(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.occupied()).isFalse();
        assertThat(response.licensePlate()).isEmpty();
        assertThat(response.priceUntilNow()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.entryTime()).isNull();
        assertThat(response.timeParked()).isNull();

        verify(spotRepository, times(1)).findByLatAndLng(requestDTO.lat(), requestDTO.lng());
        verify(parkingSessionRepository, times(1)).findFirstBySpotAndExitTimeIsNotNull(mockSpot);
    }

    @Test
    void getSpotStatus_whenSpotIsOccupied_shouldReturnStatusAsOccupiedWithSessionData() {
        final var requestDTO = new SpotStatusRequestDTO(-23.561684, -46.655981);
        final var entryTime = LocalDateTime.parse("2025-06-07T12:00:00");
        final var parkedTime = LocalDateTime.parse("2025-06-07T12:05:00");

        final var mockSpot = new Spot(1, null, requestDTO.lat(), requestDTO.lng(), false);

        final var mockParkingSession = new ParkingSession(new Entry(
                LICENSE_PLATE, entryTime, EventType.ENTRY
        ));
        mockParkingSession.setSpot(mockSpot);
        mockParkingSession.setParkedTime(parkedTime);
        mockParkingSession.calculateBasePrice(80.0, new BigDecimal("10.0"));

        when(spotRepository.findByLatAndLng(requestDTO.lat(), requestDTO.lng())).thenReturn(mockSpot);
        when(parkingSessionRepository.findFirstBySpotAndExitTimeIsNotNull(mockSpot)).thenReturn(mockParkingSession);

        SpotStatusResponseDTO response = spotService.getSpotStatus(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.occupied()).isTrue();
        assertThat(response.licensePlate()).isEqualTo(LICENSE_PLATE);
        assertThat(response.entryTime()).isEqualTo(entryTime);
        assertThat(response.timeParked()).isEqualTo(parkedTime);

        verify(spotRepository, times(1)).findByLatAndLng(requestDTO.lat(), requestDTO.lng());
        verify(parkingSessionRepository, times(1)).findFirstBySpotAndExitTimeIsNotNull(mockSpot);
    }
}
