package com.modasby.gestaoestacionamentos.web.controller;

import com.modasby.gestaoestacionamentos.service.SpotService;
import com.modasby.gestaoestacionamentos.web.dto.spotStatus.SpotStatusRequestDTO;
import com.modasby.gestaoestacionamentos.web.dto.spotStatus.SpotStatusResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotController {
    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @PostMapping("/spot-status")
    public ResponseEntity<SpotStatusResponseDTO> getSpotStatus(@RequestBody SpotStatusRequestDTO dto) {
        return ResponseEntity.ok(this.spotService.getSpotStatus(dto));
    }
}
