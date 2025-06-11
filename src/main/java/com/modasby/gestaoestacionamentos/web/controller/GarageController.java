package com.modasby.gestaoestacionamentos.web.controller;

import com.modasby.gestaoestacionamentos.service.GarageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GarageController {

    private final GarageService garageService;

    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }

    @GetMapping("/garage")
    public ResponseEntity<?> getGarage() {
        return ResponseEntity.ok(this.garageService.initializeGarage());
    }
}
