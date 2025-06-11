package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSessionStatus;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EntryHandlerTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private EntryHandler entryHandler;

    @Captor
    private ArgumentCaptor<ParkingSession> parkingSessionCaptor;

    private Entry entryEvent;
    private LocalDateTime entryTime;

    @BeforeEach
    public void setUp() {
        entryTime = LocalDateTime.now();
        entryEvent = new Entry("ZUL0001", entryTime, EventType.ENTRY);
    }

    @Test
    public void testHandle_ShouldCreateAndSaveParkingSession() throws Exception {
        // Act
        entryHandler.handle(entryEvent);

        // Assert
        verify(parkingSessionRepository).save(parkingSessionCaptor.capture());

        ParkingSession capturedSession = parkingSessionCaptor.getValue();

        // Use reflection to verify private fields
        java.lang.reflect.Field licensePlateField = ParkingSession.class.getDeclaredField("licensePlate");
        licensePlateField.setAccessible(true);
        assertEquals("ZUL0001", licensePlateField.get(capturedSession));

        java.lang.reflect.Field entryTimeField = ParkingSession.class.getDeclaredField("entryTime");
        entryTimeField.setAccessible(true);
        assertEquals(entryTime, entryTimeField.get(capturedSession));

        java.lang.reflect.Field statusField = ParkingSession.class.getDeclaredField("status");
        statusField.setAccessible(true);
        assertEquals(ParkingSessionStatus.ENTERED, statusField.get(capturedSession));
    }
}