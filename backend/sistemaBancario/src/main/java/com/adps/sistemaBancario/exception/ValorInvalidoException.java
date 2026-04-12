package com.adps.sistemaBancario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValorInvalidoException extends NegocioException{
    public ValorInvalidoException() {
        super("O valor da operação tem que ser maior que zero!");
    }
}
