package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.StatusConta;
import com.adps.sistemaBancario.dto.CriarContaDTO;
import com.adps.sistemaBancario.exception.NegocioException;
import com.adps.sistemaBancario.exception.SenhaInvalidaException;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.repository.ContaRepository;
import org.springframework.stereotype.Service;


@Service
public class CadastroService {
    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    public CadastroService(ContaRepository contaRepository, ClienteRepository clienteRepository, ClienteService clienteService) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
    }

    public Conta cadastrarCliente(CriarContaDTO criarContaDTO) {
        boolean encontrouNumero = false;
        boolean encontrouLetra = false;
        boolean encontrouEspecial = false;

        if (criarContaDTO.getSenhaCliente() == null || criarContaDTO.getSenhaCliente().isBlank()) {
            throw new SenhaInvalidaException();
        }
        if (criarContaDTO.getSenhaCliente().length() < 8) {
            throw new SenhaInvalidaException();
        }
        for(char c : criarContaDTO.getSenhaCliente().toCharArray()) {
            if (Character.isDigit(c)) encontrouNumero = true;
            if (Character.isUpperCase(c)) encontrouLetra = true;
            if ("!@#$%&*?".contains(String.valueOf(c))) encontrouEspecial = true;
        }
        if(!encontrouEspecial || !encontrouLetra || !encontrouNumero) {
            throw new SenhaInvalidaException();
        }

        if(criarContaDTO.getEmailCliente() == null || criarContaDTO.getEmailCliente().isBlank() || !criarContaDTO.getEmailCliente().contains("@")){
            throw new NegocioException("Digite um email válido! Precisa conter '@'");
        }

        if (clienteRepository.existsByEmail(criarContaDTO.getEmailCliente())){
            throw new NegocioException("Esse email já foi cadastrado!");
        }
        if(criarContaDTO.getNomeCliente() == null || criarContaDTO.getNomeCliente().isBlank() || criarContaDTO.getNomeCliente().length() < 3){
            throw new NegocioException("Digite o nome do cliente! Precisa ser maior que 3 caracteres");
        }

        Cliente cliente = clienteService.criarCliente(
                criarContaDTO.getNomeCliente(),
                criarContaDTO.getEmailCliente(),
                criarContaDTO.getSenhaCliente());
        Conta conta = new Conta(cliente);
        conta.setStatusConta(StatusConta.ATIVADA);
        clienteRepository.save(cliente);
        return contaRepository.save(conta);
    }

}
