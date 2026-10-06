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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Transacao transacao = new Transacao(conta, null, valor, TransacaoTipo.SAQUE, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
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
        Transacao transacao = new Transacao(conta, null, valor, TransacaoTipo.DEPOSITO, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        Transacao transacaoFeita = transacaoRepository.save(transacao);

        return new TransacaoResponseDTO(
                transacaoFeita.getId(),
                transacaoFeita.getTransacaoTipo(),
                transacaoFeita.getDataHoraTransacao(),
                transacaoFeita.getValor()

        );
    }

    private String definirTipoHistorico(Transacao transacao, Conta contaCliente){
        if(transacao.getTransacaoTipo() != TransacaoTipo.TRANSFERENCIA){
            return transacao.getTransacaoTipo().toString();
        }

        if(transacao.getContaOrigem().getId_conta().equals(contaCliente.getId_conta())){
            return "TRANSFERENCIA_SAIDA";
        }

        if(transacao.getContaDestino().getId_conta().equals(contaCliente.getId_conta())){
            return "TRANSFERENCIA_ENTRADA";
        }

        return "TRANSFERENCIA";
    }

    public List<HistoricoDTO> listarTransacoes(TransacoesFiltroRequest transacoesFiltro, Cliente cliente) {

        Conta contaCliente = contaRepository.findByCliente(cliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta"));

        List<String> tipos = transacoesFiltro.tipo();

        List<String> tiposTransacao = tipos == null
                ? null
                : tipos.stream()
                     .filter(tipo ->
                             "TRANSFERENCIA".equalsIgnoreCase(tipo) ||
                             "TRANSFERENCIA_SAIDA".equalsIgnoreCase(tipo) ||
                             "TRANSFERENCIA_ENTRADA".equalsIgnoreCase(tipo) ||
                             "DEPOSITO".equalsIgnoreCase(tipo) ||
                             "SAQUE".equalsIgnoreCase(tipo))
                .toList();

        TransacoesFiltroRequest filtroTransacao = new TransacoesFiltroRequest(
                tiposTransacao,
                transacoesFiltro.dataRealizada(),
                transacoesFiltro.valor()
        );

        boolean temPagamento = tipos != null &&
                tipos.stream().anyMatch(tipo -> "PAGAMENTO".equalsIgnoreCase(tipo));

        boolean temTransacao = tipos != null &&
                tipos.stream().anyMatch(tipo -> "TRANSFERENCIA_SAIDA".equalsIgnoreCase(tipo) || "TRANSFERENCIA_ENTRADA".equalsIgnoreCase(tipo)
                        || "TRANSFERENCIA".equalsIgnoreCase(tipo) ||
                        "DEPOSITO".equalsIgnoreCase(tipo) || "SAQUE".equalsIgnoreCase(tipo));

        boolean nenhumTipo = tipos == null || tipos.isEmpty();

        List<HistoricoDTO> historico = new ArrayList<>();


        if (nenhumTipo) {
            List<Pagamento> pagamentos = pagamentoRepository.findAll(
                    TransacoesSpecification.comFiltrosPagamento(transacoesFiltro, cliente)
            );
            List<Transacao> transacoes = transacaoRepository.findAll(
                    TransacoesSpecification.comFiltros(filtroTransacao, cliente)
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
                            p.getCliente().getNome(),
                            null,
                            null
                    ))
                    .toList();
            List<HistoricoDTO> historicoTransacoes = transacoes.stream()
                    .map(p -> new HistoricoDTO(
                            p.getId(),
                            p.getDataHoraTransacao(),
                            p.getValor(),
                            definirTipoHistorico(p, contaCliente),
                            null,
                            null,
                            null,
                            null,
                            null,
                            p.getContaOrigem() != null
                                    ? p.getContaOrigem().getId_conta()
                                    : null,
                            p.getContaDestino() != null
                                    ? p.getContaDestino().getId_conta()
                                    : null
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
                                t.getCliente().getNome(),
                                null,
                                null

                        ))
                        .toList());

            }

            if (temTransacao) {
                List<Transacao> transacoes = transacaoRepository.findAll(
                        TransacoesSpecification.comFiltros(filtroTransacao, cliente)
                );
                historico.addAll(transacoes.stream()
                        .map(t -> new HistoricoDTO(
                                t.getId(),
                                t.getDataHoraTransacao(),
                                t.getValor(),
                                definirTipoHistorico(t, contaCliente),
                                null,
                                null,
                                null,
                                null,
                                null,
                                t.getContaOrigem() != null
                                        ? t.getContaOrigem().getId_conta()
                                        : null,
                                t.getContaDestino() != null
                                        ? t.getContaDestino().getId_conta()
                                        : null
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

        if (origem.getStatusConta() == StatusConta.DELETADA ||
                destino.getStatusConta() == StatusConta.DELETADA) {
            throw new ContaDeletadaException();
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

        transacaoRepository.save(new Transacao(origem, destino, valor, TransacaoTipo.TRANSFERENCIA, LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))));
    }
}
