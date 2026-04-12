package com.adps.sistemaBancario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecursoNaoEncontradoException extends NegocioException{
    public RecursoNaoEncontradoException(String recurso){
        super(recurso + " não encontrada!");
    }
}
