package com.modasby.gestaoestacionamentos.client.simulator;

import com.modasby.gestaoestacionamentos.client.simulator.dto.SimulatorConfigDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        value = "simulator",
        url = "http://localhost:3000"
)
public interface SimulatorClient {
    @GetMapping("/garage")
    SimulatorConfigDTO getConfig();
}
