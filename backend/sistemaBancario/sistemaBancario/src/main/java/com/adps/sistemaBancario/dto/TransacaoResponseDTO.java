package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.TransacaoTipo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoResponseDTO {
    private Long id;
    private BigDecimal valor;
    private TransacaoTipo transacaoTipo;
    private LocalDateTime dataHoraTransacao;

    public TransacaoResponseDTO(Long id, TransacaoTipo transacaoTipo, LocalDateTime dataHoraTransacao, BigDecimal valor) {
        this.id = id;
        this.transacaoTipo = transacaoTipo;
        this.dataHoraTransacao = dataHoraTransacao;
        this.valor = valor;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TransacaoTipo getTransacaoTipo() {
        return transacaoTipo;
    }

    public void setTransacaoTipo(TransacaoTipo transacaoTipo) {
        this.transacaoTipo = transacaoTipo;
    }

    public LocalDateTime getDataHoraTransacao() {
        return dataHoraTransacao;
    }

    public void setDataHoraTransacao(LocalDateTime dataHoraTransacao) {
        this.dataHoraTransacao = dataHoraTransacao;
    }
}
