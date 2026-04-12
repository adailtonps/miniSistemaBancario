package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.StatusConta;

import java.math.BigDecimal;

public record ContaDTO(
        Long id,
        StatusConta StatusConta,
        BigDecimal Saldo,
        String emailCliente,
        String nomeCliente
) {
}
