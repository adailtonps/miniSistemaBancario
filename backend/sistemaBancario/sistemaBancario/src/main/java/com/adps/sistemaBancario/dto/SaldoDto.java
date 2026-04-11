package com.adps.sistemaBancario.dto;

import java.math.BigDecimal;

public class SaldoDto {
    private BigDecimal saldo;

    public SaldoDto(BigDecimal saldo){
        this.saldo = saldo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

}
