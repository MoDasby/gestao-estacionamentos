package com.modasby.gestaoestacionamentos.domain.parking;

import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
public class ParkingSession {
    public ParkingSession() {}

    public ParkingSession(Entry entry) {
        this.licensePlate = entry.licensePlate();
        this.entryTime = entry.entryTime();
        this.status = ParkingSessionStatus.ENTERED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime parkedTime;
    private LocalDateTime exitTime;
    private BigDecimal basePrice;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @Enumerated(EnumType.STRING)
    private ParkingSessionStatus status;

    public void setStatus(ParkingSessionStatus status) {
        this.status = status;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        if (Objects.isNull(spot)) return;

        this.spot = spot;
        this.spot.setOccupied(true);
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void calculateBasePrice(Double occupancyPercentage, BigDecimal garageBasePrice) {
        if (occupancyPercentage < 25) {
            this.basePrice = garageBasePrice.multiply(new BigDecimal("0.9"));
        } else if (occupancyPercentage < 50) {
             this.basePrice = garageBasePrice;
        } else if (occupancyPercentage < 75) {
            this.basePrice = garageBasePrice.multiply(new BigDecimal("1.1"));
        } else {
            this.basePrice = garageBasePrice.multiply(new BigDecimal("1.25"));
        }
    }

    public BigDecimal getPriceUntilNow() {
        LocalDateTime endTime = Objects.isNull(this.exitTime)
                ? LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
                : this.exitTime;

        Duration parkingTime = Duration.between(
                this.parkedTime,
                endTime
        );

        double billedHours = Math.ceil(parkingTime.toMinutes() / 60.0);

        return basePrice.multiply(billedHours >= 1
                ? BigDecimal.valueOf(billedHours)
                : BigDecimal.ONE);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public ParkingSessionStatus getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getParkedTime() {
        return parkedTime;
    }

    public void setParkedTime(LocalDateTime parkedTime) {
        this.parkedTime = parkedTime;
    }
}
