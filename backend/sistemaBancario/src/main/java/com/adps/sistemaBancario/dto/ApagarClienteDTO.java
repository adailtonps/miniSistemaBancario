package com.adps.sistemaBancario.dto;

public class ApagarClienteDTO {
    private String email;
    private String senha;

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
