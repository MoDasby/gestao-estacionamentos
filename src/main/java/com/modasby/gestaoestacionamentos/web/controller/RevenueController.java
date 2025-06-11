package com.modasby.gestaoestacionamentos.web.controller;

import com.modasby.gestaoestacionamentos.service.RevenueService;
import com.modasby.gestaoestacionamentos.web.dto.revenue.RevenueRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RevenueController {
    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }


    @PostMapping("/revenue")
    public ResponseEntity<?> getRevenue(@RequestBody RevenueRequestDTO dto) {
        return ResponseEntity.ok(this.revenueService.calculateSectorRevenue(dto));
    }
}
