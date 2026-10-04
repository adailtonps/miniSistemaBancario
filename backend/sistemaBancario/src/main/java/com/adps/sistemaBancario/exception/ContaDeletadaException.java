package com.adps.sistemaBancario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class ContaDeletadaException extends NegocioException {
    public ContaDeletadaException() {
        super("A conta de destino está deletada!");

    }
}
