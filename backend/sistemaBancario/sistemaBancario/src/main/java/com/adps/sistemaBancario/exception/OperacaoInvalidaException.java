package com.adps.sistemaBancario.exception;

public class OperacaoInvalidaException extends NegocioException{
    public OperacaoInvalidaException(String msg){
        super(msg);
    }
}
