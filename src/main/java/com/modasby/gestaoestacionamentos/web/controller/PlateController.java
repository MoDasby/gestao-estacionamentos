package com.modasby.gestaoestacionamentos.web.controller;

import com.modasby.gestaoestacionamentos.service.ParkingSessionService;
import com.modasby.gestaoestacionamentos.web.dto.plateStatus.PlateStatusRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlateController {
    private final ParkingSessionService parkingSessionService;

    public PlateController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @PostMapping("/plate-status")
    public ResponseEntity<?> getPlateStatus(@RequestBody PlateStatusRequestDTO request) {
        return ResponseEntity.ok(this.parkingSessionService.getPlateStatus(request.licensePlate()));
    }
}
