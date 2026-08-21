package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoHistoricoDYO(
        String idPedido,
        String codigoPagamento,
        LocalDateTime dataPagamento,
        BigDecimal valor,
        StatusPagamento statusPagamento
) {
}
