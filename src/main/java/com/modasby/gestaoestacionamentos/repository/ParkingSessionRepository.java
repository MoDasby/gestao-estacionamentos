package com.modasby.gestaoestacionamentos.repository;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Integer> {
    ParkingSession findByLicensePlate(String licensePlate);
    ParkingSession findFirstBySpotAndExitTimeIsNotNull(Spot spot);
    List<ParkingSession> findAllBySpot_Garage_SectorAndExitTimeBetween(String spotGarageSector, LocalDateTime parkedTimeAfter, LocalDateTime parkedTimeBefore);

    @Query("SELECT COUNT(p) FROM ParkingSession p " +
            "WHERE p.spot.garage.sector = :sector " +
            "AND p.entryTime < :timeOfEntry " +
            "AND (p.exitTime IS NULL OR p.exitTime > :timeOfEntry)")
    long countActiveSessionsAtTime(@Param("sector") String sector, @Param("timeOfEntry") LocalDateTime timeOfEntry);
}
