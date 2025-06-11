package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSessionStatus;
import com.modasby.gestaoestacionamentos.event.model.EventType;
import com.modasby.gestaoestacionamentos.event.model.Exit;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import com.modasby.gestaoestacionamentos.service.ParkingSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExitHandlerTest {

    @Mock
    private ParkingSessionService parkingSessionService;

    @Mock
    private ParkingSession parkingSession;

    @InjectMocks
    private ExitHandler exitHandler;

    private Exit exitEvent;
    private LocalDateTime exitTime;

    @BeforeEach
    public void setUp() {
        exitTime = LocalDateTime.now();
        exitEvent = new Exit("ZUL0001", exitTime, EventType.EXIT);
    }

    @Test
    public void testHandle_ShouldUpdateParkingSession() {
        when(parkingSessionService.findByLicensePlate("ZUL0001")).thenReturn(parkingSession);

        exitHandler.handle(exitEvent);

        verify(parkingSession).setStatus(ParkingSessionStatus.EXITED);
        verify(parkingSession).setExitTime(exitTime);
    }
}