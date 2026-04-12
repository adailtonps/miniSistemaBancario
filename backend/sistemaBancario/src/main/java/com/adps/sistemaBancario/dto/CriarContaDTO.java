package com.adps.sistemaBancario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CriarContaDTO {

    @JsonProperty("nome")
    private String nomeCliente;

    @JsonProperty("email")
    private String emailCliente;

    @JsonProperty("senha")
    private String senhaCliente;

    public String getSenhaCliente() {
        return senhaCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setSenhaCliente(String senhaCliente) {
        this.senhaCliente = senhaCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
}
