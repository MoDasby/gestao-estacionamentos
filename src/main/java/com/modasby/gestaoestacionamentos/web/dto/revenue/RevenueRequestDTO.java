package com.modasby.gestaoestacionamentos.web.dto.revenue;

import java.time.LocalDate;

public record RevenueRequestDTO(
        LocalDate date,
        String sector
) {
}
