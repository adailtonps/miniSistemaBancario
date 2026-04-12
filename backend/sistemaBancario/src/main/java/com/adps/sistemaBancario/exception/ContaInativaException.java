package com.adps.sistemaBancario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ContaInativaException extends NegocioException{
    public ContaInativaException() {
        super("A conta está desativada! Algumas funcionalidade não estão disponíveis!");
    }
}
