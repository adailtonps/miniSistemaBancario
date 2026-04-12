package com.adps.sistemaBancario.controller;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.dto.SenhaDto;
import com.adps.sistemaBancario.dto.TransferenciaDTO;
import com.adps.sistemaBancario.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransferenciaController{
    private final TransacaoService transacaoService;

    public TransferenciaController(TransacaoService transacaoService){
        this.transacaoService = transacaoService;
    }

    @PostMapping("/transferencia")
    ResponseEntity<String> transferir (
            @AuthenticationPrincipal Cliente clienteLogado,
            @RequestBody TransferenciaDTO dto
            ) {
        transacaoService.transferir(
                clienteLogado,
                dto.getDestinoId(),
                dto.getValor(),
                dto.getSenha()
        );
        return ResponseEntity.ok("Transferência realizada com sucesso!");
    }
}

