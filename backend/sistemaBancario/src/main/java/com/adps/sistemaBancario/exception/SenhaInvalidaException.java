package com.adps.sistemaBancario.exception;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException() {
        super("Senha inválida! PRECISA CONTER, NO MÍNIMO, 8 CARACTERES, UM NÚMERO, UMA LETRA MAIÚSCULA E UM CARACTER ESPECIAIL (!@#$%%&*?)");
    }
}
