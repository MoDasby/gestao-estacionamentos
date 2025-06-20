package com.modasby.gestaoestacionamentos.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record Entry(
        @JsonProperty("license_plate") String licensePlate,
        @JsonProperty("entry_time") LocalDateTime entryTime,
        @JsonProperty("event_type") EventType eventType
) implements WebhookEvent {
}
