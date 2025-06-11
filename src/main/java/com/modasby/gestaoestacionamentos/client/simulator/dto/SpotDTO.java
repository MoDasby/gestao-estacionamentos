package com.modasby.gestaoestacionamentos.client.simulator.dto;

public record SpotDTO(
        Integer id,
        String sector,
        Double lat,
        Double lng,
        Boolean occupied
) {
}
