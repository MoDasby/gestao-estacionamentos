package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.domain.parking.ParkingSessionStatus;
import com.modasby.gestaoestacionamentos.event.model.Exit;
import com.modasby.gestaoestacionamentos.service.ParkingSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExitHandler implements WebhookHandler<Exit> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ParkingSessionService parkingSessionService;

    public ExitHandler(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @Override
    public void handle(Exit event) {
        ParkingSession parkingSession = this.parkingSessionService.findByLicensePlate(event.licensePlate());

        parkingSession.setStatus(ParkingSessionStatus.EXITED);
        parkingSession.setExitTime(event.exitTime());

        logger.info("Exit parking for plate: {}", event.licensePlate());
    }

    @Override
    public Class<Exit> getEventType() {
        return Exit.class;
    }
}
