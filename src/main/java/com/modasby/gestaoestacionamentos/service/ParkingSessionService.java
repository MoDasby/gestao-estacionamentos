package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.web.dto.plateStatus.PlateStatusResponseDTO;
import com.modasby.gestaoestacionamentos.web.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;

    public ParkingSessionService(
            ParkingSessionRepository parkingSessionRepository
    ) {
        this.parkingSessionRepository = parkingSessionRepository;
    }

    public ParkingSession findByLicensePlate(String licensePlate) {
        ParkingSession parkingSession = this.parkingSessionRepository.findByLicensePlate(licensePlate);

        if (parkingSession == null) {
            throw new EntityNotFoundException("Parking session not found for license plate: " + licensePlate);
        }

        return parkingSession;
    }

    public PlateStatusResponseDTO getPlateStatus(String licensePlate) {
        ParkingSession parkingSession = this.findByLicensePlate(licensePlate);

        Spot spot = parkingSession.getSpot();

        return new PlateStatusResponseDTO(
                parkingSession.getLicensePlate(),
                parkingSession.getPriceUntilNow(),
                parkingSession.getEntryTime(),
                parkingSession.getParkedTime(),
                spot != null ? parkingSession.getSpot().getLat() : null,
                spot != null ? parkingSession.getSpot().getLng() : null
        );
    }
}
