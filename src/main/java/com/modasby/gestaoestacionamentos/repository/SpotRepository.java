package com.modasby.gestaoestacionamentos.repository;

import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Integer> {
    Spot findByLatAndLng(double lat, double lng);
    Integer countByGarageSectorAndOccupied(String sector, Boolean occupied);
}
