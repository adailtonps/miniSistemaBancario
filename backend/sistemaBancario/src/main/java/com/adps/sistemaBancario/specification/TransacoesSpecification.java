package com.adps.sistemaBancario.specification;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Pagamento;
import com.adps.sistemaBancario.domain.Transacao;
import com.adps.sistemaBancario.domain.TransacaoTipo;
import com.adps.sistemaBancario.dto.TransacoesFiltroRequest;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransacoesSpecification {
    public static Specification<Transacao> comFiltros(TransacoesFiltroRequest filtro, Cliente cliente) {
        return Specification
                .where(clienteContem(cliente))
                .and(tipoContem(filtro.tipo(), cliente))
                .and(dataRealizadaContem(filtro.dataRealizada()))
                .and(valorContem(filtro.valor()));
    }

    public static Specification<Pagamento> comFiltrosPagamento(TransacoesFiltroRequest filtro, Cliente cliente) {
        return Specification
                .where(clienteContemPagamento(cliente))
                .and(dataRealizadaContemPagamento(filtro.dataRealizada()))
                .and(valorContemPagamento(filtro.valor()));
    }

    private static Specification<Pagamento> clienteContemPagamento(Cliente cliente) {
        return (root, query, cb) -> {
            return cb.equal(
                    root.get("cliente").get("id"),cliente.getId()
            );
        };
    }

    private static Specification<Transacao> clienteContem(Cliente cliente) {
        return (root, query, cb) -> {

            var origem = root.join("contaOrigem", JoinType.LEFT);
            var destino = root.join("contaDestino",JoinType.LEFT);

            var clienteOrigem = origem.join("cliente",JoinType.LEFT);
            var clienteDestino = destino.join("cliente", JoinType.LEFT);

            return cb.or(
                    cb.equal(clienteOrigem.get("id"), cliente.getId()),
                    cb.equal(clienteDestino.get("id"), cliente.getId())
            );
        };
    }

    private static Specification<Transacao> tipoContem(List<String> tipos, Cliente cliente) {
        return (root, query, cb) -> {
            if (tipos == null || tipos.isEmpty()) {
                return null;
            }

            List<jakarta.persistence.criteria.Predicate> filtros = new ArrayList<>();

            for (String tipo : tipos) {
                if ("TRANSFERENCIA_ENTRADA".equalsIgnoreCase(tipo)) {
                    filtros.add(
                            cb.and(
                                    cb.equal(
                                            root.get("transacaoTipo"),
                                            TransacaoTipo.TRANSFERENCIA
                                    ),
                                    cb.equal(
                                            root.get("contaDestino").get("cliente").get("id"),
                                            cliente.getId()
                                    )
                            )
                    );
                } else if ("TRANSFERENCIA_SAIDA".equalsIgnoreCase(tipo)) {
                    filtros.add(
                            cb.and(
                                    cb.equal(
                                            root.get("contaOrigem").get("cliente").get("id"),
                                            cliente.getId()
                                    )
                            )
                    );
                } else {
                    TransacaoTipo tipoEnum = TransacaoTipo.valueOf(tipo.toUpperCase());

                    filtros.add(
                            cb.equal(
                                    root.get("transacaoTipo"),
                                    tipoEnum
                            )
                    );
                }
            }
            return cb.or(filtros.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private static Specification<Transacao> dataRealizadaContem(LocalDateTime dataRealizada) {
        return(root, query, cb) -> {
          if(dataRealizada == null){
              return null;
          }

          LocalDateTime inicio = dataRealizada.toLocalDate().atStartOfDay();
          LocalDateTime fim = inicio.plusDays(1);

          return cb.between((root.get("dataHoraTransacao")),inicio,fim);
        };
    }

    private static Specification<Transacao> valorContem(BigDecimal valor) {
        return (root, query, cb) -> {
            if (valor == null || valor.compareTo(BigDecimal.ZERO) == 0) {
                return null;
            }
            return cb.equal((root.get("valor")), valor);
        };
    }

    private static Specification<Pagamento> dataRealizadaContemPagamento(LocalDateTime dataRealizada) {
        return(root, query, cb) -> {
          if(dataRealizada == null){
              return null;
          }

            LocalDateTime inicio = dataRealizada.toLocalDate().atStartOfDay();
            LocalDateTime fim = inicio.plusDays(1);

          return cb.between((root.get("dataPagamento")), inicio, fim);
        };
    }

    private static Specification<Pagamento> valorContemPagamento(BigDecimal valor) {
        return(root, query, cb) -> {
          if(valor == null || valor.compareTo(BigDecimal.ZERO) == 0){
              return null;
          }
          return cb.equal(root.get("valorTotal"), valor);
        };
    }
}
