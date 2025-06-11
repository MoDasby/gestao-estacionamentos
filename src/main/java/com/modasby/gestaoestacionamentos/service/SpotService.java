package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.repository.SpotRepository;
import com.modasby.gestaoestacionamentos.web.dto.spotStatus.SpotStatusRequestDTO;
import com.modasby.gestaoestacionamentos.web.dto.spotStatus.SpotStatusResponseDTO;
import com.modasby.gestaoestacionamentos.web.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class SpotService {
    private final SpotRepository spotRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    public SpotService(SpotRepository spotRepository, ParkingSessionRepository parkingSessionRepository) {
        this.spotRepository = spotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    public SpotStatusResponseDTO getSpotStatus(SpotStatusRequestDTO dto) {
        Spot spot = this.spotRepository.findByLatAndLng(dto.lat(), dto.lng());

        if (Objects.isNull(spot)) {
            throw new EntityNotFoundException("Spot not found");
        }

        ParkingSession parkingSession = this.parkingSessionRepository.findFirstBySpotAndExitTimeIsNotNull(spot);

        if (Objects.isNull(parkingSession)) {
            return new SpotStatusResponseDTO(
                    spot.isOccupied(),
                    "",
                    BigDecimal.ZERO,
                    null,
                    null
            );
        }

        return new SpotStatusResponseDTO(
                parkingSession.getSpot().isOccupied(),
                parkingSession.getLicensePlate(),
                parkingSession.getPriceUntilNow(),
                parkingSession.getEntryTime(),
                parkingSession.getParkedTime()
        );
    }
}
