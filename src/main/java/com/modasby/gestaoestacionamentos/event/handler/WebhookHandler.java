package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.event.model.WebhookEvent;

public interface WebhookHandler<T extends WebhookEvent> {
    void handle(T event);

    Class<T> getEventType();
}
