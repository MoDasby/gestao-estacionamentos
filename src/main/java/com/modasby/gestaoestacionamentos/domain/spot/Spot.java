package com.modasby.gestaoestacionamentos.domain.spot;

import com.modasby.gestaoestacionamentos.domain.garage.Garage;
import jakarta.persistence.*;
import org.hibernate.query.NativeQuery;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Spot {
    public Spot() {}

    public Spot(Integer id, Garage garage, Double lat, Double lng, Boolean occupied) {
        this.id = id;
        this.garage = garage;
        this.lat = lat;
        this.lng = lng;
        this.occupied = occupied;
    }

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "garage_id")
    private Garage garage;

    private Double lat;
    private Double lng;
    private Boolean occupied;

    public Garage getGarage() {
        return this.garage;
    }

    public Boolean isOpen(LocalTime entryTime) {
        LocalTime openHour = this.garage.getOpenHour();
        LocalTime closeHour = this.garage.getCloseHour();

        return entryTime.isAfter(openHour) && entryTime.isBefore(closeHour);
    }

    public Boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Integer getId() {
        return id;
    }
}
