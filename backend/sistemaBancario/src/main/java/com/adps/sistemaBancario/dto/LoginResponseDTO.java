package com.adps.sistemaBancario.dto;

public class LoginResponseDTO {
    private String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
