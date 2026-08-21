package com.adps.sistemaBancario.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="transacao")
public class Transacao {
    @Id
    private String id;

    @PrePersist
    public void gerarId(){
        if(this.id == null){
            this.id = UUID.randomUUID().toString()
                    .replace("-","")
                    .toUpperCase()
                    .substring(0,8);
        }
    }

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
        this.dataHoraTransacao = LocalDateTime.now();
        this.valor = valor;
        this.transacaoTipo = transacaoTipo;
    }

    public String getId() {
        return id;
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
