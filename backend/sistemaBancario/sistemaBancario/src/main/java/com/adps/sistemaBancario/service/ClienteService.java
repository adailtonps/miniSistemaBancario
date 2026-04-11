package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.StatusConta;
import com.adps.sistemaBancario.dto.ClienteAtualizarDto;
import com.adps.sistemaBancario.exception.NegocioException;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.repository.ContaRepository;
import com.adps.sistemaBancario.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service

public class ClienteService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final ClienteRepository clienteRepository;
    private final JWTService jwtService;

    @Autowired
    private final PasswordEncoder encoder;

    public ClienteService(ContaRepository contaRepository, TransacaoRepository transacaoRepository, ClienteRepository clienteRepository, PasswordEncoder encoder, JWTService jwtService) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.clienteRepository = clienteRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public Cliente criarCliente(String nomeCliente, String emailCliente, String senhaCliente) {
        String senhaOriginal = senhaCliente;
        String senhaCriptografada = encoder.encode(senhaOriginal);
        Cliente cliente = new Cliente();
        cliente.setSenhaCliente(senhaCriptografada);
        cliente.setNome(nomeCliente);
        cliente.setEmail(emailCliente);
        return clienteRepository.save(cliente);

    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new NegocioException("Nenhum cliente encontrado!"));
    }

    public Cliente findByEmail(String email) {
        return clienteRepository.findByEmail(email).orElseThrow(() -> new NegocioException("Nenhum cliente encontrado!"));
    }

    public void atualizarNome(Cliente cliente, String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new NegocioException("Nome inválido!");
        }

        cliente.setNome(novoNome);
    }

    public void atualizarEmail(Cliente cliente, String novoEmail) {
        if (novoEmail == null || novoEmail.isBlank()) {
            throw new NegocioException("Email inválido!");
        }
        if (clienteRepository.findByEmail(novoEmail).isPresent() && !cliente.getEmail().equals(novoEmail)) {
            throw new NegocioException("Email já existente!");
        }

        cliente.alterarEmail(novoEmail);
    }

    public Cliente atualizarCliente(Cliente cliente, ClienteAtualizarDto atualizarDto) {
        boolean quarAtualizaremail = atualizarDto.getEmail() != null && !atualizarDto.getEmail().isBlank();
        boolean querAtualizarnome = atualizarDto.getNome() != null && !atualizarDto.getNome().isBlank();

        if (!querAtualizarnome && !quarAtualizaremail) {
            throw new NegocioException("Nenhum dado para atualizar!");
        }

        if (!encoder.matches(atualizarDto.getSenha(), cliente.getSenhaCliente())) {
            throw new NegocioException("Senha incorreta!");
        }

        if (atualizarDto.getNome() != null && !atualizarDto.getNome().isBlank()) {
            atualizarNome(cliente, atualizarDto.getNome());
        }

        if (atualizarDto.getEmail() != null && !atualizarDto.getEmail().isBlank()) {
            atualizarEmail(cliente, atualizarDto.getEmail());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void deletar(Cliente clienteLogado, String email, String senha) {

        if (!clienteLogado.getEmail().equals(email)) {
            throw new NegocioException("Email não confere com cliente logado!");
        }

        if (!encoder.matches(senha, clienteLogado.getSenhaCliente())) {
            throw new NegocioException("Senha incorreta!");
        }

        Conta conta = contaRepository.findByCliente(clienteLogado)
                .orElseThrow(() -> new NegocioException("Conta não encontrada!"));

        if (conta.getStatusConta() == StatusConta.ATIVADA) {
            throw new NegocioException("Cliente possui conta ativa!");
        }
        transacaoRepository.deleteByConta(conta);
        contaRepository.delete(conta);
        clienteRepository.delete(clienteLogado);
    }
}
