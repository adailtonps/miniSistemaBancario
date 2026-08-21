package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.*;
import com.adps.sistemaBancario.dto.HistoricoDTO;
import com.adps.sistemaBancario.dto.TransacaoResponseDTO;
import com.adps.sistemaBancario.dto.PagamentoHistoricoDYO;
import com.adps.sistemaBancario.exception.*;
import com.adps.sistemaBancario.repository.ContaRepository;
import com.adps.sistemaBancario.repository.PagamentoRepository;
import com.adps.sistemaBancario.repository.TransacaoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PagamentoRepository pagamentoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository, PasswordEncoder passwordEncoder, PagamentoRepository pagamentoRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public TransacaoResponseDTO sacar(Cliente cliente, BigDecimal valor) {
        Conta conta = contaRepository.findByCliente(cliente).orElseThrow(() ->
                new RecursoNaoEncontradoException("Conta"));
        if (conta.getStatusConta() == StatusConta.DESATIVADA) {
            throw new ContaInativaException();
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException();
        }
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException();
        }
        conta.debitar(valor);
        contaRepository.save(conta);
        Transacao transacao = new Transacao(conta, valor, TransacaoTipo.SAQUE);
        Transacao saqueFeito = transacaoRepository.save(transacao);

        return new TransacaoResponseDTO(
                saqueFeito.getId(),
                saqueFeito.getTransacaoTipo(),
                saqueFeito.getDataHoraTransacao(),
                saqueFeito.getValor()
        );
    }

    @Transactional
    public TransacaoResponseDTO depositar(Cliente cliente, BigDecimal valor) {
        Conta conta = contaRepository.findByCliente(cliente).orElseThrow(() ->
                new RecursoNaoEncontradoException("Conta"));
        if (conta.getStatusConta() == StatusConta.DESATIVADA) {
            throw new ContaInativaException();
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException();
        }

        conta.creditar(valor);
        contaRepository.save(conta);
        Transacao transacao = new Transacao(conta, valor, TransacaoTipo.DEPOSITO);
        Transacao transacaoFeita = transacaoRepository.save(transacao);

        return new TransacaoResponseDTO(
                transacaoFeita.getId(),
                transacaoFeita.getTransacaoTipo(),
                transacaoFeita.getDataHoraTransacao(),
                transacaoFeita.getValor()

        );
    }


    @Transactional(readOnly = true)
    public List<HistoricoDTO> historico(Cliente cliente) {
        Conta conta = contaRepository.findByCliente(cliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta"));
        List<Transacao> transacaoHistorico = transacaoRepository
                .findByContaOrderByDataHoraTransacaoDesc(conta);

        List<Pagamento> pagamentos = pagamentoRepository.findByClienteOrderByDataPagamentoDesc(cliente);

        List<HistoricoDTO> historico = transacaoHistorico.stream()
                .map(t -> new HistoricoDTO(
                        t.getId(),
                        t.getDataHoraTransacao(),
                        t.getValor(),
                        t.getTransacaoTipo().toString(),
                        null
                )).collect(Collectors.toList());

        pagamentos.forEach(
                p ->
                        historico.add(new HistoricoDTO(
                                        p.getIdPedido(),
                                        p.getDataPagamento(),
                                        p.getValorTotal(),
                                        "PAGAMENTO",
                                        p.getCodigoPagamento()
                                )
                        ));

                historico.sort(
                        Comparator.comparing(HistoricoDTO::data).reversed()
                );
        return historico;
    }

    @Transactional
    public void transferir(Cliente clienteLogado, Long destinoId, BigDecimal valor, String senha) {
        Conta origem = contaRepository.findByCliente(clienteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta"));

        if (origem == null) {
            throw new OperacaoInvalidaException("Digite a conta de origem!");
        }

        Conta destino = contaRepository.findById(destinoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino"));

        if (destino == null) {
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
        if (!passwordEncoder.matches(senha, clienteLogado.getSenhaCliente())) {
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
