package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.domain.garage.Garage;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSessionStatus;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.event.model.Parked;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.repository.SpotRepository;
import com.modasby.gestaoestacionamentos.service.ParkingSessionService;
import com.modasby.gestaoestacionamentos.web.exception.GarageFullCapacityException;
import com.modasby.gestaoestacionamentos.web.exception.SpotNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkedHandlerTest {

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private ParkingSessionService parkingSessionService;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private ParkingSession parkingSession;

    @Mock
    private Spot spot;

    @Mock
    private Garage garage;

    @InjectMocks
    private ParkedHandler parkedHandler;

    private Parked parkedEvent;

    @BeforeEach
    void setUp() {
        parkedEvent = new Parked("ZUL0001", -23.5, -46.6, EventType.PARKED);

        when(parkingSessionService.findByLicensePlate("ZUL0001")).thenReturn(parkingSession);

    }

    @Test
    void testHandle_Success_ShouldUpdateParkingSession() {
        when(parkingSession.getEntryTime()).thenReturn(LocalDateTime.now());
        when(spotRepository.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(spot);
        when(spot.isOccupied()).thenReturn(false);
        when(spot.isOpen(any(LocalTime.class))).thenReturn(true);
        when(spot.getGarage()).thenReturn(garage);
        when(garage.getSector()).thenReturn("A");
        when(garage.getMaxCapacity()).thenReturn(100);
        when(garage.getBasePrise()).thenReturn(BigDecimal.TEN);
        when(parkingSessionRepository.countActiveSessionsAtTime(anyString(), any(LocalDateTime.class))).thenReturn(50L);

        parkedHandler.handle(parkedEvent);

        verify(parkingSession).calculateBasePrice(50.0, BigDecimal.TEN);
        verify(parkingSession).setStatus(ParkingSessionStatus.PARKED);
        verify(parkingSession).setSpot(spot);
        verify(parkingSession).setParkedTime(any(LocalDateTime.class));
    }

    @Test
    void testHandle_WhenSpotNotFound_ShouldThrowException() {
        when(spotRepository.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(null);

        assertThrows(SpotNotAvailableException.class, () -> parkedHandler.handle(parkedEvent));
    }

    @Test
    void testHandle_WhenSpotIsOccupied_ShouldThrowException() {
        when(spotRepository.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(spot);
        when(spot.isOccupied()).thenReturn(true);

        assertThrows(SpotNotAvailableException.class, () -> parkedHandler.handle(parkedEvent));
    }

    @Test
    void testHandle_WhenSpotIsClosed_ShouldThrowException() {
        when(parkingSession.getEntryTime()).thenReturn(LocalDateTime.now());
        when(spotRepository.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(spot);
        when(spot.isOccupied()).thenReturn(false);
        when(spot.isOpen(any(LocalTime.class))).thenReturn(false);

        assertThrows(SpotNotAvailableException.class, () -> parkedHandler.handle(parkedEvent));
    }

    @Test
    void testHandle_WhenGarageIsFull_ShouldThrowException() {
        when(parkingSession.getEntryTime()).thenReturn(LocalDateTime.now());
        when(spotRepository.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(spot);
        when(spot.isOccupied()).thenReturn(false);
        when(spot.isOpen(any(LocalTime.class))).thenReturn(true);
        when(spot.getGarage()).thenReturn(garage);
        when(garage.getSector()).thenReturn("A");
        when(garage.getMaxCapacity()).thenReturn(100);
        when(parkingSessionRepository.countActiveSessionsAtTime(anyString(), any(LocalDateTime.class))).thenReturn(100L);

        assertThrows(GarageFullCapacityException.class, () -> parkedHandler.handle(parkedEvent));
    }
}