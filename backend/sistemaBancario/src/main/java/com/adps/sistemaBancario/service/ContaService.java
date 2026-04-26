package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.StatusConta;
import com.adps.sistemaBancario.dto.ContaDTO;
import com.adps.sistemaBancario.dto.SaldoDto;
import com.adps.sistemaBancario.exception.*;
import com.adps.sistemaBancario.repository.ContaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class ContaService {
    private ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public ContaDTO minhaConta(Cliente clienteLogado) {
        Conta conta = contaRepository.findByCliente(clienteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta"));

        Cliente cliente = conta.getCliente();

        return new ContaDTO(
                conta.getId_conta(),
                conta.getStatusConta(),
                conta.getSaldo(),
                cliente.getEmail(),
                cliente.getNome()
        );
    }

    public SaldoDto consultarSaldo(Cliente clienteLogado) {
        Conta conta = buscarContaDoCliente(clienteLogado);
        return new SaldoDto(conta.getSaldo());
    }

    public Conta buscarContaDoCliente(Cliente clienteLogado){
        return contaRepository.findByCliente(clienteLogado)
                .orElseThrow(() -> new NegocioException("Conta não encontrada!"));
    }


    public void desativarConta(Cliente cliente) {
        Conta conta = contaRepository.findByCliente(cliente).orElseThrow(
                () -> new NegocioException("Conta não encontrada!"));
        if(conta.getStatusConta() == StatusConta.DESATIVADA ){
            throw new NegocioException("Conta já desativada!");
        }

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new NegocioException("Conta com saldo positivo não pode ser desativada!"
            );
        }
        conta.setStatusConta(StatusConta.DESATIVADA);
        contaRepository.save(conta);
    }

    public void ativarConta(Cliente cliente) {
        Conta conta = contaRepository.findByCliente(cliente).orElseThrow(
                () -> new NegocioException("Conta não encontrada!"));
        if(conta.getStatusConta() == StatusConta.ATIVADA){
            throw new NegocioException("Conta já ativada!");
        }
        conta.setStatusConta(StatusConta.ATIVADA);
        contaRepository.save(conta);
    }
}
