package com.adps.sistemaBancario.controller;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.dto.ClienteAtualizarDto;
import com.adps.sistemaBancario.dto.ClienteResponseDTO;
import com.adps.sistemaBancario.dto.SenhaDto;
import com.adps.sistemaBancario.service.ClienteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PatchMapping("/me")
    public ClienteResponseDTO atualizarCliente(
            @AuthenticationPrincipal Cliente clienteLogado,
            @RequestBody ClienteAtualizarDto atualizarDto
            ){
        Cliente clienteAtualizado = clienteService.atualizarCliente(clienteLogado, atualizarDto);
        return new ClienteResponseDTO(
                clienteAtualizado.getId(),
                clienteAtualizado.getNome(),
                clienteAtualizado.getEmail()
        );
    }
}
