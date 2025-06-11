package com.modasby.gestaoestacionamentos.event.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "event_type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Entry.class, name = "ENTRY"),
        @JsonSubTypes.Type(value = Exit.class, name = "EXIT"),
        @JsonSubTypes.Type(value = Parked.class, name = "PARKED")
})
public interface WebhookEvent {
    EventType eventType();
}
