package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.TransacaoTipo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoDTO(
        String id,
        LocalDateTime data,
        BigDecimal valor,
        String tipo,
        String codigoPagamento
) {
}
