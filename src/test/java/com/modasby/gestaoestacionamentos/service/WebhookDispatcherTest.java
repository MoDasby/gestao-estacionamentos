package com.modasby.gestaoestacionamentos.service;

import com.modasby.gestaoestacionamentos.event.handler.EntryHandler;
import com.modasby.gestaoestacionamentos.event.handler.ExitHandler;
import com.modasby.gestaoestacionamentos.event.handler.ParkedHandler;
import com.modasby.gestaoestacionamentos.event.handler.WebhookHandler;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.event.model.Exit;
import com.modasby.gestaoestacionamentos.event.model.Parked;
import com.modasby.gestaoestacionamentos.web.exception.InvalidWebhookBodyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebhookDispatcherTest {

    @Mock
    private EntryHandler entryHandler;

    @Mock
    private ExitHandler exitHandler;

    @Mock
    private ParkedHandler parkedHandler;

    private WebhookDispatcher webhookDispatcher;

    @BeforeEach
    public void setUp() {
        when(entryHandler.getEventType()).thenReturn(Entry.class);
        when(exitHandler.getEventType()).thenReturn(Exit.class);
        when(parkedHandler.getEventType()).thenReturn(Parked.class);

        List<WebhookHandler<?>> handlers = List.of(entryHandler, exitHandler, parkedHandler);
        webhookDispatcher = new WebhookDispatcher(handlers);
        webhookDispatcher.init();
    }

    @Test
    public void testDispatch_Entry_ShouldCallEntryHandler() {
        LocalDateTime entryTime = LocalDateTime.now();
        Entry entry = new Entry("ZUL0001", entryTime, EventType.ENTRY);

        webhookDispatcher.dispatch(entry);

        verify(entryHandler).handle(entry);

        verify(exitHandler, never()).handle(any(Exit.class));
        verify(parkedHandler, never()).handle(any(Parked.class));
    }

    @Test
    public void testDispatch_Exit_ShouldCallExitHandler() {
        LocalDateTime exitTime = LocalDateTime.now();
        Exit exit = new Exit("ZUL0001", exitTime, EventType.EXIT);

        webhookDispatcher.dispatch(exit);

        verify(exitHandler).handle(exit);

        verify(entryHandler, never()).handle(any(Entry.class));
        verify(parkedHandler, never()).handle(any(Parked.class));
    }

    @Test
    public void testDispatch_Parked_ShouldCallParkedHandler() {
        Parked parked = new Parked("ZUL0001", 10.0, 20.0, EventType.PARKED);

        webhookDispatcher.dispatch(parked);

        verify(parkedHandler).handle(parked);

        verify(entryHandler, never()).handle(any(Entry.class));
        verify(exitHandler, never()).handle(any(Exit.class));
    }

    @Test
    public void testDispatch_NullEvent_ShouldThrowException() {
        assertThrows(InvalidWebhookBodyException.class, () -> webhookDispatcher.dispatch(null));
    }
}