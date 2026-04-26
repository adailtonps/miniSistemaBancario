package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.TransacaoTipo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class TransacaoResponseDTO {
    private Long id;
    private BigDecimal valor;
    private TransacaoTipo transacaoTipo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Sao_Paulo")
    private OffsetDateTime dataHoraTransacao;

    public TransacaoResponseDTO(Long id, TransacaoTipo transacaoTipo, OffsetDateTime dataHoraTransacao, BigDecimal valor) {
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

    public OffsetDateTime getDataHoraTransacao() {
        return dataHoraTransacao;
    }

    public void setDataHoraTransacao(OffsetDateTime dataHoraTransacao) {
        this.dataHoraTransacao = dataHoraTransacao;
    }

}
