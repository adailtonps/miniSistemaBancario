package com.adps.sistemaBancario.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class TransferenciaDTO {
    private Long id;
    private Long destinoId;
    private BigDecimal valor;
    private String senha;

    public String getSenha() {
        return senha;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(Long destinoId) {
        this.destinoId = destinoId;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
