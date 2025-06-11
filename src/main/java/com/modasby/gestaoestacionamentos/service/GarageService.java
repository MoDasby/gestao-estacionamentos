package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.client.simulator.SimulatorClient;
import com.modasby.gestaoestacionamentos.client.simulator.dto.SimulatorConfigDTO;
import com.modasby.gestaoestacionamentos.domain.garage.Garage;
import com.modasby.gestaoestacionamentos.domain.spot.Spot;
import com.modasby.gestaoestacionamentos.repository.GarageRepository;
import com.modasby.gestaoestacionamentos.repository.SpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GarageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimulatorClient simulatorClient;
    private final GarageRepository garageRepository;
    private final SpotRepository spotRepository;

    public GarageService(SimulatorClient simulatorClient, GarageRepository garageRepository, SpotRepository spotRepository) {
        this.simulatorClient = simulatorClient;
        this.garageRepository = garageRepository;
        this.spotRepository = spotRepository;
    }

    public SimulatorConfigDTO initializeGarage() {
        SimulatorConfigDTO simulatorConfig = this.simulatorClient.getConfig();

        List<Garage> garages = simulatorConfig
                .garage()
                .parallelStream()
                .map(g -> new Garage(
                        g.sector(),
                        g.basePrice(),
                        g.maxCapacity(),
                        g.openHour(),
                        g.closeHour(),
                        g.durationLimitMinutes()
                ))
                .toList();

        Map<String, Garage> garageMap = simulatorConfig
                .garage()
                .parallelStream()
                .map(g -> new Garage(
                        g.sector(),
                        g.basePrice(),
                        g.maxCapacity(),
                        g.openHour(),
                        g.closeHour(),
                        g.durationLimitMinutes()
                ))
                .collect(Collectors.toMap(Garage::getSector, Function.identity()));

        List<Spot> spots = simulatorConfig
                .spots()
                .parallelStream()
                .map(s -> new Spot(
                        s.id(),
                        garageMap.get(s.sector()),
                        s.lat(),
                        s.lng(),
                        s.occupied()
                ))
                .toList();

        this.garageRepository.saveAll(garages);
        this.spotRepository.saveAll(spots);
        logger.info("Garage and spots created");

        return simulatorConfig;
    }
}
