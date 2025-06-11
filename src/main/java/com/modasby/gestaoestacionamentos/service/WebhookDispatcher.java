package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.event.handler.WebhookHandler;
import com.modasby.gestaoestacionamentos.event.model.WebhookEvent;
import com.modasby.gestaoestacionamentos.web.exception.InvalidWebhookBodyException;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WebhookDispatcher {

    private final List<WebhookHandler<? extends WebhookEvent>> handlers;
    private Map<Class<? extends WebhookEvent>, WebhookHandler<? extends WebhookEvent>> handlerMap;

    public WebhookDispatcher(List<WebhookHandler<? extends WebhookEvent>> handlers) {
        this.handlers = handlers;
    }

    @PostConstruct
    public void init() {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(WebhookHandler::getEventType, Function.identity()));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public <T extends WebhookEvent> void dispatch(T event) {
        if (event == null) {
            throw new InvalidWebhookBodyException("Event cannot be null");
        }

        WebhookHandler<T> handler = (WebhookHandler<T>) handlerMap.get(event.getClass());

        if (handler != null) {
            handler.handle(event);
        } else {
            throw new InvalidWebhookBodyException("No handler found for event type: " + event.getClass().getSimpleName());
        }
    }
}
