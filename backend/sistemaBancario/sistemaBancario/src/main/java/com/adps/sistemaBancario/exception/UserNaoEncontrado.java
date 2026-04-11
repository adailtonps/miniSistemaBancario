package com.adps.sistemaBancario.exception;

public class UserNaoEncontrado extends RuntimeException {
    public UserNaoEncontrado(String message) {
        super(message);
    }
}
