package com.adps.sistemaBancario.controller;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.dto.HistoricoDTO;
import com.adps.sistemaBancario.dto.TransacoesFiltroRequest;
import com.adps.sistemaBancario.service.TransacaoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historico")
public class TransacoesController {
    private final TransacaoService transacaoService;

    @GetMapping()
    public List<HistoricoDTO> listarTransacoes(TransacoesFiltroRequest filtro, @AuthenticationPrincipal Cliente clienteLogado){
        return transacaoService.listarTransacoes(filtro, clienteLogado);
    }
}
