package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.web.dto.plateStatus.PlateStatusResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingSessionServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private ParkingSessionService parkingSessionService;

    private static final String LICENSE_PLATE = "ZUL0001";
    private ParkingSession parkingSession;
    private BigDecimal expectedCalculatedPrice;

    @BeforeEach
    void setUp() {
        Spot mockSpot = new Spot(1, null, -23.561684, -46.655981, true);

        parkingSession = new ParkingSession(
                new Entry(LICENSE_PLATE, LocalDateTime.now(), EventType.ENTRY)
        );

        parkingSession.setSpot(mockSpot);
        parkingSession.calculateBasePrice(80.0, new BigDecimal("10.0"));

        expectedCalculatedPrice = parkingSession.getBasePrice();
    }

    @Test
    void getPlateStatus_WhenParkedFor45Minutes_ShouldChargeForOneHour() {
        parkingSession.setParkedTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(45));
        parkingSession.setExitTime(null);
        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(parkingSession);

        PlateStatusResponseDTO response = parkingSessionService.getPlateStatus(LICENSE_PLATE);

        assertNotNull(response);
        assertEquals(0, expectedCalculatedPrice.compareTo(response.priceUntilNow()), "O preço para 45min deve ser o valor de 1h cheia");
        assertEquals(LICENSE_PLATE, response.licensePlate());
    }

    @Test
    void getPlateStatus_WhenParkedFor90Minutes_ShouldChargeForTwoHours() {
        parkingSession.setParkedTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(90));
        parkingSession.setExitTime(null);
        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(parkingSession);

        PlateStatusResponseDTO response = parkingSessionService.getPlateStatus(LICENSE_PLATE);

        assertNotNull(response);
        BigDecimal expectedPrice = expectedCalculatedPrice.multiply(new BigDecimal("2"));
        assertEquals(0, expectedPrice.compareTo(response.priceUntilNow()), "O preço para 90min deve ser o de 2h cheias");
    }

    @Test
    void getPlateStatus_WhenCarHasAlreadyExited_ShouldCalculatePriceBasedOnExitTime() {
        LocalDateTime parkedTime = LocalDateTime.of(2025, 6, 10, 10, 0, 0);
        LocalDateTime exitTime = LocalDateTime.of(2025, 6, 10, 11, 30, 0); // 90 minutos
        parkingSession.setParkedTime(parkedTime);
        parkingSession.setExitTime(exitTime);
        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(parkingSession);

        PlateStatusResponseDTO response = parkingSessionService.getPlateStatus(LICENSE_PLATE);

        assertNotNull(response);
        BigDecimal expectedPrice = expectedCalculatedPrice.multiply(new BigDecimal("2"));
        assertEquals(0, expectedPrice.compareTo(response.priceUntilNow()), "O preço para um carro que já saiu deve ser fixo");
    }
}
