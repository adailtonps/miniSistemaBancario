package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.*;
import com.adps.sistemaBancario.dto.*;
import com.adps.sistemaBancario.exception.*;
import com.adps.sistemaBancario.repository.ContaRepository;
import com.adps.sistemaBancario.repository.PagamentoRepository;
import com.adps.sistemaBancario.repository.TransacaoRepository;
import com.adps.sistemaBancario.specification.TransacoesSpecification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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


    public List<HistoricoDTO> listarTransacoes(TransacoesFiltroRequest transacoesFiltro, Cliente cliente) {
        List<String> tipos = transacoesFiltro.tipo();

        boolean temPagamento = tipos != null &&
                tipos.stream().anyMatch(tipo -> "PAGAMENTO".equalsIgnoreCase(tipo));

        boolean temTransacao = tipos != null &&
                tipos.stream().anyMatch(tipo -> "TRANSFERENCIA".equalsIgnoreCase(tipo) ||
                        "DEPOSITO".equalsIgnoreCase(tipo) || "SAQUE".equalsIgnoreCase(tipo));

        boolean nenhumTipo = tipos == null || tipos.isEmpty();

        List<HistoricoDTO> historico = new ArrayList<>();


        if (nenhumTipo) {
            List<Pagamento> pagamentos = pagamentoRepository.findAll(
                    TransacoesSpecification.comFiltrosPagamento(transacoesFiltro, cliente)
            );
            List<Transacao> transacoes = transacaoRepository.findAll(
                    TransacoesSpecification.comFiltros(transacoesFiltro, cliente)
            );
            List<HistoricoDTO> historicoPagamentos = pagamentos.stream()
                    .map(p -> new HistoricoDTO(
                            p.getIdPedido(),
                            p.getDataPagamento(),
                            p.getValorTotal(),
                            "PAGAMENTO",
                            p.getCodigoPagamento(),
                            p.getIdSolicitante(),
                            p.getNomeSolicitante(),
                            p.getCliente().getId(),
                            p.getCliente().getNome()
                    ))
                    .toList();
            List<HistoricoDTO> historicoTransacoes = transacoes.stream()
                    .map(p -> new HistoricoDTO(
                            p.getId(),
                            p.getDataHoraTransacao(),
                            p.getValor(),
                            p.getTransacaoTipo().toString(), null,
                            null,
                            null,
                            null,
                            null
                    ))
                    .toList();

            historico.addAll(historicoTransacoes);
            historico.addAll(historicoPagamentos);

        } else {
            if (temPagamento) {
                List<Pagamento> pagamentos = pagamentoRepository.findAll(
                        TransacoesSpecification.comFiltrosPagamento(transacoesFiltro, cliente)
                );
                historico.addAll(pagamentos.stream()
                        .map(t -> new HistoricoDTO(
                                t.getIdPedido(),
                                t.getDataPagamento(),
                                t.getValorTotal(),
                                "PAGAMENTO",
                                t.getCodigoPagamento(),
                                t.getIdSolicitante(),
                                t.getNomeSolicitante(),
                                t.getCliente().getId(),
                                t.getCliente().getNome()
                        ))
                        .toList());

            }

            if (temTransacao) {
                List<Transacao> transacoes = transacaoRepository.findAll(
                        TransacoesSpecification.comFiltros(transacoesFiltro, cliente)
                );
                historico.addAll(transacoes.stream()
                        .map(t -> new HistoricoDTO(
                                t.getId(),
                                t.getDataHoraTransacao(),
                                t.getValor(),
                                t.getTransacaoTipo().toString(),
                                null,
                                null,
                                null,
                                null,
                                null
                        ))
                        .toList());

            }
        }

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
