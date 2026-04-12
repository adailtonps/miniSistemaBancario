package com.adps.sistemaBancario.exception;

public class SaldoInsuficienteException extends NegocioException{
    public SaldoInsuficienteException() {
        super("Saldo insuficiente para realizar a operação!");
    }
}
