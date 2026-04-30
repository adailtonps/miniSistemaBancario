package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.*;
import com.adps.sistemaBancario.dto.TransacaoResponseDTO;
import com.adps.sistemaBancario.exception.*;
import com.adps.sistemaBancario.repository.ContaRepository;
import com.adps.sistemaBancario.repository.TransacaoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public TransacaoService(TransacaoRepository transacaoRepository,  ContaRepository contaRepository, PasswordEncoder passwordEncoder) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TransacaoResponseDTO sacar(Cliente cliente, BigDecimal valor){
        Conta conta = contaRepository.findByCliente(cliente).orElseThrow(() ->
                new RecursoNaoEncontradoException("Conta"));
        if(conta.getStatusConta() == StatusConta.DESATIVADA){
            throw new ContaInativaException();
        }
        if(valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new ValorInvalidoException();
        }
        if(conta.getSaldo().compareTo(valor) < 0){
            throw new SaldoInsuficienteException();
        }
        conta.debitar(valor);
        contaRepository.save(conta);
        Transacao transacao = new Transacao(conta, valor, TransacaoTipo.SAQUE);
        Transacao saqueFeito = transacaoRepository.save(transacao);

        return new TransacaoResponseDTO(
                saqueFeito.getIdTransacao(),
                saqueFeito.getTransacaoTipo(),
                saqueFeito.getDataHoraTransacao(),
                saqueFeito.getValor()
        );
    }

    @Transactional
    public TransacaoResponseDTO depositar(Cliente cliente, BigDecimal valor){
        Conta conta = contaRepository.findByCliente(cliente).orElseThrow(() ->
                new RecursoNaoEncontradoException("Conta"));
        if(conta.getStatusConta() == StatusConta.DESATIVADA){
            throw new ContaInativaException();
        }
        if(valor == null || valor.compareTo(BigDecimal.ZERO)<= 0){
            throw new ValorInvalidoException();
        }

        conta.creditar(valor);
        contaRepository.save(conta);
        Transacao transacao = new Transacao(conta, valor, TransacaoTipo.DEPOSITO);
        Transacao transacaoFeita = transacaoRepository.save(transacao);

        return new TransacaoResponseDTO(
                transacaoFeita.getIdTransacao(),
                transacaoFeita.getTransacaoTipo(),
                transacaoFeita.getDataHoraTransacao(),
                transacaoFeita.getValor()
        );
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> historico(Cliente cliente){
        Conta conta = contaRepository.findByCliente(cliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta"));
        List<Transacao> transacaoHistorico = transacaoRepository
                .findByContaOrderByDataHoraTransacaoDesc(conta);

        return transacaoHistorico.stream()
                .map(t -> new TransacaoResponseDTO(
                        t.getIdTransacao(),
                        t.getTransacaoTipo(),
                        t.getDataHoraTransacao(),
                        t.getValor()
                ))
                .toList();
    }

    @Transactional
    public void transferir(Cliente clienteLogado, Long destinoId, BigDecimal valor, String senha) {
        Conta origem = contaRepository.findByCliente(clienteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta"));

        if(origem == null){
            throw new OperacaoInvalidaException("Digite a conta de origem!");
        }

        Conta destino = contaRepository.findById(destinoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino"));

        if(destino == null){
            throw new OperacaoInvalidaException("Digite a conta de destino!");
        }

        if (origem.getId_conta().equals(destino.getId_conta())) {
            throw new OperacaoInvalidaException(
                    "Conta de destino não pode ser a mesma que a sua: IDs IGUAIS!");
        }

        if (origem.getStatusConta() == StatusConta.DESATIVADA ||
                destino.getStatusConta() == StatusConta.DESATIVADA) {
            throw new ContaInativaException();
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException();
        }

        if (origem.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException();
        }
        if(!passwordEncoder.matches(senha, clienteLogado.getSenhaCliente())){
            throw new OperacaoInvalidaException("Senha incorreta!");
        }

        origem.debitar(valor);
        destino.creditar(valor);

        contaRepository.save(origem);
        contaRepository.save(destino);

        transacaoRepository.save(new Transacao(origem, valor, TransacaoTipo.TRANSFERENCIA_SAIDA));
        transacaoRepository.save(new Transacao(destino, valor, TransacaoTipo.TRANSFERENCIA_ENTRADA));
    }
}
