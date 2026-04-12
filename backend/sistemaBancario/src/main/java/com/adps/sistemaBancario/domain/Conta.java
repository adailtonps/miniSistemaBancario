package com.adps.sistemaBancario.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="conta")
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Long id;

    @Column(nullable=false)
    private BigDecimal saldo = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Conta(){
        this.statusConta = StatusConta.ATIVADA;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatusConta statusConta = StatusConta.ATIVADA;

    public Conta(Cliente cliente){
        this.cliente = cliente;
        this.saldo = BigDecimal.ZERO;
        this.statusConta = StatusConta.ATIVADA;
    }

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transacao> historico = new ArrayList<>();


    public void debitar(BigDecimal valor){
        this.saldo = this.saldo.subtract(valor);
    }

    public StatusConta getStatusConta() {
        return statusConta;
    }

    public List<Transacao> getHistorico() {
        return historico;
    }

    public void setHistorico(List<Transacao> historico) {
        this.historico = historico;
    }

    public void setStatusConta(StatusConta statusConta) {
        this.statusConta = statusConta;
    }

    public void creditar(BigDecimal valor){
        this.saldo = this.saldo.add(valor);
    }


    public Long getId_conta() {
        return id;
    }


    public BigDecimal getSaldo() {
        return saldo;
    }


    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

}
