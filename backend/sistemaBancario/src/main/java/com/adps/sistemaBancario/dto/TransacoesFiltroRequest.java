package com.adps.sistemaBancario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacoesFiltroRequest(
        String tipo,
        LocalDateTime dataRealizada,
        BigDecimal valor
) {
}
