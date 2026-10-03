package com.adps.sistemaBancario.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
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
    @JoinColumn(name="conta_origem_id")
    private Conta contaOrigem;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="conta_destino_id")
    private Conta contaDestino;

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

    public Transacao(Conta contaOrigem, Conta contaDestino, BigDecimal valor, TransacaoTipo transacaoTipo, LocalDateTime dataHoraTransacao) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.transacaoTipo = transacaoTipo;
        this.dataHoraTransacao = dataHoraTransacao;
    }

    public Transacao(Conta conta, BigDecimal valor, TransacaoTipo transacaoTipo) {
        this.conta = conta;
        this.valor = valor;
        this.transacaoTipo = transacaoTipo;
    }

    public String getId() {
        return id;
    }

    public Conta getConta() {
        return contaOrigem;
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
