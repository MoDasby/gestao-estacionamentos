package com.modasby.gestaoestacionamentos.domain.parking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParkingSessionTest {

    @ParameterizedTest
    @CsvSource({
            "10.0, 10.0, 9.0",
            "24.9, 10.0, 9.0",
            "25.0, 10.0, 10.0",
            "49.9, 10.0, 10.0",
            "50.0, 10.0, 11.0",
            "74.9, 10.0, 11.0",
            "75.0, 10.0, 12.5",
            "99.9, 10.0, 12.5"
    })
    public void testCalculateBasePrice_ShouldApplyCorrectPricing(double occupancyPercentage, double basePrice, double expectedPrice) throws Exception {
        ParkingSession parkingSession = new ParkingSession();
        BigDecimal garageBasePrice = new BigDecimal(String.valueOf(basePrice));

        parkingSession.calculateBasePrice(occupancyPercentage, garageBasePrice);

        BigDecimal actualBasePrice = parkingSession.getBasePrice();

        assertEquals(new BigDecimal(String.valueOf(expectedPrice)).stripTrailingZeros(), 
                    actualBasePrice.stripTrailingZeros());
    }


    @ParameterizedTest
    @CsvSource({
            // occupancyPercentage, garageBasePrice, parkedMinutesAgo, exitMinutesAgo, expectedPrice
            "10, 10.00, 180, 0, 27.00",   // 10% ocupação → 10 * 0.9 = 9.00 → 3h * 9 = 27
            "30, 10.00, 90, 0, 20.00",    // 30% → 10 * 1.0 = 10.00 → 2h * 10 = 20
            "60, 10.00, 30, 0, 11.00",    // 60% → 10 * 1.1 = 11.00 → 1h * 11 = 11
            "85, 8.00, 59, 0, 10.00",     // 85% → 8 * 1.25 = 10.00 → <1h → mínimo 1h * 10
            "80, 20.00, 120, 0, 50.00"    // 80% → 20 * 1.25 = 25.00 → 2h * 25 = 50
    })
    void testGetPriceUntilNow_CalculatedBasePrice(Double occupancyPercentage,
                                                  String garageBasePriceStr,
                                                  int parkedMinutesAgo,
                                                  int exitMinutesAgo,
                                                  String expectedPriceStr) {
        ParkingSession session = new ParkingSession();

        BigDecimal garageBasePrice = new BigDecimal(garageBasePriceStr);
        BigDecimal expectedPrice = new BigDecimal(expectedPriceStr);

        LocalDateTime now = LocalDateTime.now();
        session.setParkedTime(now.minusMinutes(parkedMinutesAgo));
        session.setExitTime(now.minusMinutes(exitMinutesAgo));
        session.calculateBasePrice(occupancyPercentage, garageBasePrice);

        BigDecimal actual = session.getPriceUntilNow();

        assertEquals(new BigDecimal(String.valueOf(expectedPrice)).stripTrailingZeros(),
                actual.stripTrailingZeros());
    }

    @Test
    void testGetPriceUntilNow_ExitTimeIsNull_UsesNow() {
        ParkingSession session = new ParkingSession();
        session.setParkedTime(LocalDateTime.now().minusHours(1));
        session.setExitTime(null);
        session.calculateBasePrice(50.0, new BigDecimal("10.00")); // deve usar 11.00

        BigDecimal actual = session.getPriceUntilNow();

        BigDecimal minimum = new BigDecimal("11.00");
        assertEquals(0, actual.compareTo(minimum) >= 0 ? 0 : 1);
    }
}
