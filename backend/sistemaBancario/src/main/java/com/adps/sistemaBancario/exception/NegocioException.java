package com.adps.sistemaBancario.exception;

public class NegocioException extends RuntimeException {
    public  NegocioException(String mensagem) {
        super(mensagem);
    }
}
