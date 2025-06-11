package com.modasby.gestaoestacionamentos.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Parked(
        @JsonProperty("license_plate") String licensePlate,
        @JsonProperty("lat") double lat,
        @JsonProperty("lng") double lng,
        @JsonProperty("event_type") EventType eventType
) implements WebhookEvent {
}
