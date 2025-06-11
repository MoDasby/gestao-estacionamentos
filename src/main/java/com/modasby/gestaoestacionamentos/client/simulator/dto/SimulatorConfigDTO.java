package com.modasby.gestaoestacionamentos.client.simulator.dto;

import java.util.List;

public record SimulatorConfigDTO(
        List<GarageDTO> garage,
        List<SpotDTO> spots
) {
}
