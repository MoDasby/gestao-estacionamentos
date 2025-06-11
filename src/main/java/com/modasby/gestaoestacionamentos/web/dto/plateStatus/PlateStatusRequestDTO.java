package com.modasby.gestaoestacionamentos.web.dto.plateStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlateStatusRequestDTO(@JsonProperty("license_plate") String licensePlate) {
}
