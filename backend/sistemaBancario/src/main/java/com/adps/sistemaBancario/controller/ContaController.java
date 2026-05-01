package com.adps.sistemaBancario.controller;

import com.adps.sistemaBancario.domain.*;
import com.adps.sistemaBancario.dto.*;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.repository.ContaRepository;
import com.adps.sistemaBancario.service.ClienteService;
import com.adps.sistemaBancario.service.ContaService;
import com.adps.sistemaBancario.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conta")
public class ContaController {
    private final ClienteService clienteService;
    private final TransacaoService transacaoService;
    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final ContaService contaService;

    public ContaController(TransacaoService transacaoService, ContaRepository contaRepository, ClienteService clienteService, ContaService contaService, ClienteRepository clienteRepository) {
        this.transacaoService = transacaoService;
        this.contaRepository = contaRepository;
        this.clienteService = clienteService;
        this.contaService = contaService;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/minha-conta")
    public ContaDTO minhaConta(
            @AuthenticationPrincipal Cliente clienteLogado){
        return contaService.minhaConta(clienteLogado);
    }

    @GetMapping("/me/saldo")
    public SaldoDto saldo(
            @AuthenticationPrincipal Cliente clienteLogado
    ) {
        return contaService.consultarSaldo(clienteLogado);
    }


    @PostMapping("/me/deposito")
    public TransacaoResponseDTO depositar(
            @RequestBody ValorDTO dto,
            @AuthenticationPrincipal Cliente clienteLogado
            ) {
        return transacaoService.depositar(clienteLogado, dto.getValor());
    }

    @PostMapping("/me/saque")
    public TransacaoResponseDTO saque(
            @AuthenticationPrincipal Cliente cliente,
            @RequestBody ValorDTO dto
    ) {
        return transacaoService.sacar(cliente, dto.getValor());
    }

    @GetMapping("/me/historico")
    public List<TransacaoResponseDTO> historico(
            @AuthenticationPrincipal Cliente clienteLogado
    ) {
        return transacaoService.historico(clienteLogado);
    }

    @PutMapping("/me/desativar")
    public ResponseEntity<Map<String, String>> desativar(
            @AuthenticationPrincipal Cliente cliente
    ) {
        contaService.desativarConta(cliente);
        return ResponseEntity.ok(Map.of
                ("mensagem","Conta Desativada com sucesso!"));
    }

    @PutMapping("/me/ativar")
    public ResponseEntity<Map<String, String>> ativar(
            @AuthenticationPrincipal Cliente cliente
    ) {
        contaService.ativarConta(cliente);
        return ResponseEntity.ok(Map.of("mensagem","Conta Ativada com sucesso!"));
    }


    @DeleteMapping("/me/delete")
    public ResponseEntity<Map<String, String>> apagar(
            @AuthenticationPrincipal Cliente cliente,
            @RequestBody ApagarClienteDTO apagarClienteDTO)
    {
        clienteService.deletar(cliente, apagarClienteDTO.getEmail(), apagarClienteDTO.getSenha());
        return ResponseEntity.ok(Map.of("mensagem","Cliente apagado com sucesso!"));
    }
}
