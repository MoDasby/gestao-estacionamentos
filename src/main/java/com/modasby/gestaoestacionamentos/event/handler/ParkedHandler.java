package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.domain.garage.Garage;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSessionStatus;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.event.model.Parked;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.repository.SpotRepository;
import com.modasby.gestaoestacionamentos.service.ParkingSessionService;
import com.modasby.gestaoestacionamentos.web.exception.GarageFullCapacityException;
import com.modasby.gestaoestacionamentos.web.exception.SpotNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class ParkedHandler implements WebhookHandler<Parked> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SpotRepository spotRepository;
    private final ParkingSessionService parkingSessionService;
    private final ParkingSessionRepository parkingSessionRepository;

    public ParkedHandler(SpotRepository spotRepository, ParkingSessionService parkingSessionService, ParkingSessionRepository parkingSessionRepository) {
        this.spotRepository = spotRepository;
        this.parkingSessionService = parkingSessionService;
        this.parkingSessionRepository = parkingSessionRepository;
    }


    @Override
    public void handle(Parked event) {
        ParkingSession parkingSession = this.parkingSessionService.findByLicensePlate(event.licensePlate());

        Spot spot = this.spotRepository.findByLatAndLng(event.lat(), event.lng());

        if (spot == null || spot.isOccupied() || !spot.isOpen(parkingSession.getEntryTime().toLocalTime())) {
            throw new SpotNotAvailableException("Spot not available");
        }

        Garage garage = spot.getGarage();

        long historicOccupancy = parkingSessionRepository.countActiveSessionsAtTime(garage.getSector(), parkingSession.getEntryTime());

        Integer maxCapacity = garage.getMaxCapacity();

        if (historicOccupancy >= maxCapacity) {
            throw new GarageFullCapacityException("Garage sector is at full capacity");
        }

        Double occupancyPercentage = (double) historicOccupancy / maxCapacity * 100.0;

        BigDecimal garageBasePrise = garage.getBasePrise();

        parkingSession.calculateBasePrice(occupancyPercentage, garageBasePrise);
        parkingSession.setStatus(ParkingSessionStatus.PARKED);
        parkingSession.setSpot(spot);
        parkingSession.setParkedTime(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));

        logger.info("Parked for plate: {}", event.licensePlate());
    }

    @Override
    public Class<Parked> getEventType() {
        return Parked.class;
    }
}
