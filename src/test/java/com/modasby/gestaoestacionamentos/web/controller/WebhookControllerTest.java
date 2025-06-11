package com.modasby.gestaoestacionamentos.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.service.WebhookDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@Import(TestConfig.class)
public class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebhookDispatcher webhookDispatcher;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testHandleWebhookEvent_Entry() throws Exception {
        // Arrange
        LocalDateTime entryTime = LocalDateTime.now();
        Entry entry = new Entry("ZUL0001", entryTime, EventType.ENTRY);
        String entryJson = objectMapper.writeValueAsString(entry);

        // Act & Assert
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson))
                .andExpect(status().isOk());

        // Verify that the dispatcher was called with the correct event
        verify(webhookDispatcher).dispatch(any(Entry.class));
    }
}
