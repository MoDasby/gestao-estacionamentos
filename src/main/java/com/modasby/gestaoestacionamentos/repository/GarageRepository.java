package com.modasby.gestaoestacionamentos.repository;

import com.modasby.gestaoestacionamentos.domain.garage.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarageRepository extends JpaRepository<Garage, String> {
}
