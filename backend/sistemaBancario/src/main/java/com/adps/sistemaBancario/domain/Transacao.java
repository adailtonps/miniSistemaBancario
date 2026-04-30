package com.adps.sistemaBancario.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name="transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransacao;


    @ManyToOne
    @JsonIgnore
    private Conta conta;

    @Column(nullable = false)
    private BigDecimal valor = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo",nullable = false)
    private TransacaoTipo transacaoTipo;

    private LocalDateTime dataHoraTransacao;

    public Transacao() {}

    public Transacao(Conta conta, BigDecimal valor, TransacaoTipo transacaoTipo) {
        this.conta = conta;
        this.dataHoraTransacao = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.valor = valor;
        this.transacaoTipo = transacaoTipo;
    }

    public Long getIdTransacao() {
        return idTransacao;
    }

    public Conta getConta() {
        return conta;
    }

    public LocalDateTime getDataHoraTransacao() {
        return dataHoraTransacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TransacaoTipo getTransacaoTipo() {
        return transacaoTipo;
    }

}
