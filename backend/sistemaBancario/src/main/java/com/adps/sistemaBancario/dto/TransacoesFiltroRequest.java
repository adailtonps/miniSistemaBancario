package com.adps.sistemaBancario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransacoesFiltroRequest(
        List<String> tipo,
        LocalDateTime dataRealizada,
        BigDecimal valor
) {
}
