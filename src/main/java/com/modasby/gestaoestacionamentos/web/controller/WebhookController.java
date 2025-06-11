package com.modasby.gestaoestacionamentos.web.controller;

import com.modasby.gestaoestacionamentos.service.WebhookDispatcher;
import com.modasby.gestaoestacionamentos.event.model.WebhookEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final WebhookDispatcher webhookDispatcher;

    public WebhookController(WebhookDispatcher webhookDispatcher) {
        this.webhookDispatcher = webhookDispatcher;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhookEvent(@RequestBody WebhookEvent event) {
        this.webhookDispatcher.dispatch(event);

        return ResponseEntity.ok().build();
    }
}
