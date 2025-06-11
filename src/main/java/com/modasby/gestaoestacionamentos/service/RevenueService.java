package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.web.dto.revenue.RevenueRequestDTO;
import com.modasby.gestaoestacionamentos.web.dto.revenue.RevenueResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class RevenueService {

    private final ParkingSessionRepository parkingSessionRepository;

    public RevenueService(ParkingSessionRepository parkingSessionRepository) {
        this.parkingSessionRepository = parkingSessionRepository;
    }

    public RevenueResponseDTO calculateSectorRevenue(RevenueRequestDTO dto) {
        List<ParkingSession> parkingSessions = this.parkingSessionRepository.findAllBySpot_Garage_SectorAndExitTimeBetween(
                dto.sector(),
                dto.date().atStartOfDay(),
                dto.date().atTime(LocalTime.MAX)
        );

        BigDecimal amount = parkingSessions.stream()
                .map(ParkingSession::getPriceUntilNow)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueResponseDTO(
                amount,
                "BRL",
                LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
        );
    }
}
