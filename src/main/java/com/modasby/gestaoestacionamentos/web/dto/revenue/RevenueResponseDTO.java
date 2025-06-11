package com.modasby.gestaoestacionamentos.web.dto.revenue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RevenueResponseDTO(
        BigDecimal amount,
        String currency,
        LocalDateTime timestamp
) {
}
