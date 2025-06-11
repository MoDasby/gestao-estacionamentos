package com.modasby.gestaoestacionamentos.event.handler;

import com.modasby.gestaoestacionamentos.domain.parking.ParkingSession;
import com.modasby.gestaoestacionamentos.event.model.Entry;
import com.modasby.gestaoestacionamentos.repository.ParkingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EntryHandler implements WebhookHandler<Entry> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ParkingSessionRepository parkingSessionRepository;

    public EntryHandler(ParkingSessionRepository parkingSessionRepository) {
        this.parkingSessionRepository = parkingSessionRepository;
    }

    @Override
    public void handle(Entry event) {
        ParkingSession parkingSession = new ParkingSession(event);

        this.parkingSessionRepository.save(parkingSession);
        logger.info("Parking session created for plate: {}", event.licensePlate());
    }

    @Override
    public Class<Entry> getEventType() {
        return Entry.class;
    }
}