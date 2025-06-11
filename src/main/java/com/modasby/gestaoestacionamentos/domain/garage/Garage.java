package com.modasby.gestaoestacionamentos.domain.garage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
public class Garage {
    public Garage() {}

    public Garage(String sector, BigDecimal basePrise, Integer maxCapacity, LocalTime openHour, LocalTime closeHour, Integer durationLimitMinutes) {
        this.sector = sector;
        this.basePrise = basePrise;
        this.maxCapacity = maxCapacity;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.durationLimitMinutes = durationLimitMinutes;
    }

    @Id
    private String sector;

    @Column(precision = 10, scale = 2)
    private BigDecimal basePrise;
    private Integer maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private Integer durationLimitMinutes;

    public String getSector() {
        return sector;
    }

    public BigDecimal getBasePrise() {
        return basePrise;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public LocalTime getOpenHour() {
        return openHour;
    }

    public LocalTime getCloseHour() {
        return closeHour;
    }

    public Integer getDurationLimitMinutes() {
        return durationLimitMinutes;
    }
}
