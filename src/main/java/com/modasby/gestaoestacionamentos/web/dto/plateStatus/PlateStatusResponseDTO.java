package com.modasby.gestaoestacionamentos.web.dto.plateStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlateStatusResponseDTO(
        @JsonProperty("license_plate") String licensePlate,
        @JsonProperty("price_until_now") BigDecimal priceUntilNow,
        @JsonProperty("entry_time") LocalDateTime entryTime,
        @JsonProperty("time_parked") LocalDateTime timeParked,
        @JsonProperty("lat") Double lat,
        @JsonProperty("lng") Double lng
) {}
