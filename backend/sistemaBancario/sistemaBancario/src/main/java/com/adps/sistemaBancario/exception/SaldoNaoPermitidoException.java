package com.adps.sistemaBancario.exception;

public class SaldoNaoPermitidoException extends RuntimeException {
    public SaldoNaoPermitidoException(String message) {
        super(message);
    }
}
