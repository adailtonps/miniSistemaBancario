package com.adps.sistemaBancario.controller;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.dto.CriarPagamentoDTO;
import com.adps.sistemaBancario.dto.PagamentoResponseDTO;
import com.adps.sistemaBancario.repository.PagamentoRepository;
import com.adps.sistemaBancario.service.GerarPagamentoService;
import com.adps.sistemaBancario.service.PagamentoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamento")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoService  pagamentoService;
    private final GerarPagamentoService gerarPagamentoService;

    @PostMapping("/gerar")
    public ResponseEntity<PagamentoResponseDTO> gerar(
            @RequestBody CriarPagamentoDTO dto){
        return ResponseEntity.ok(gerarPagamentoService.criarPagamento(dto));
    }

    @PostMapping("/realizar")
    public ResponseEntity<Void> pagar(
            @RequestBody PagamentoResponseDTO dto,
            Authentication authentication) {

        Object principal = authentication.getPrincipal();

        Cliente cliente = (Cliente) principal;

        pagamentoService.Pagar(cliente, dto);

        return ResponseEntity.ok().build();
    }
}
