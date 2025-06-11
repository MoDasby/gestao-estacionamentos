package com.modasby.gestaoestacionamentos.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record Exit(
        @JsonProperty("license_plate") String licensePlate,
        @JsonProperty("exit_time") LocalDateTime exitTime,
        @JsonProperty("event_type") EventType eventType
) implements WebhookEvent {
}
