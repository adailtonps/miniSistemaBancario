package com.adps.sistemaBancario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErroResponse> handleNegocio(NegocioException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(status)
                .body(new ErroResponse(
                        status.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> handleStatus(
            ResponseStatusException ex
    ){
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ErroResponse(
                        ex.getStatusCode().value(),
                        ex.getReason()
                ));
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ErroResponse> handleSenhaInvalida(Exception ex){
        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredential(Exception ex){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResponse(401, "Email ou senha incorretos!"));
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponse(
                        500,
                        "Erro interno do servidor"
                ));
    }
}
