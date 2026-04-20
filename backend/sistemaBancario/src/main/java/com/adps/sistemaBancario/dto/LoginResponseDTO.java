package com.adps.sistemaBancario.dto;

public class LoginResponseDTO {
    private String token;
    private String mensagem;


    public LoginResponseDTO(String mensagem,  String token) {
        this.mensagem = mensagem;
        this.token = token;
    }
    public String getToken() {
        return token;
    }
    public String getMensagem() {
        return mensagem;
    }
}
