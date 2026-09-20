package com.adps.sistemaBancario.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoDTO(
        String id,
        LocalDateTime data,
        BigDecimal valor,
        String tipo,
        String codigoPagamento,
        String idDoSolicitante,
        String nomeDoSolicitante,
        Long idDoPagador,
        String nomeDoPagador
) {
}
