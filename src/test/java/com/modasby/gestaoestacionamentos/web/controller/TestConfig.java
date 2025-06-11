package com.modasby.gestaoestacionamentos.web.controller;

import com.modasby.gestaoestacionamentos.service.WebhookDispatcher;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    
    @Bean
    public WebhookDispatcher webhookDispatcher() {
        return Mockito.mock(WebhookDispatcher.class);
    }
}