package com.adps.sistemaBancario.specification;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Pagamento;
import com.adps.sistemaBancario.domain.Transacao;
import com.adps.sistemaBancario.domain.TransacaoTipo;
import com.adps.sistemaBancario.dto.TransacoesFiltroRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransacoesSpecification {
    public static Specification<Transacao> comFiltros(TransacoesFiltroRequest filtro, Cliente cliente) {
        return Specification
                .where(clienteContem(cliente))
                .and(tipoContem(filtro.tipos()))
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
            return cb.equal(
                    root.get("conta").get("cliente").get("id"),cliente.getId()
            );
        };
    }

    private static Specification<Transacao> tipoContem(List<String> tipos) {
        return(root, query, cb) -> {
          if(tipos == null || tipos.isEmpty()){
              return null;
          }

            List<TransacaoTipo> tiposEnum = tipos.stream()
                    .map(tipo -> TransacaoTipo.valueOf(tipo.toUpperCase()))
                    .toList();

          return root.get("transacaoTipo").in(tiposEnum);
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
        return(root, query, cb) -> {
          if(valor == null || valor.compareTo(BigDecimal.ZERO) == 0){
              return null;
          }
          return cb.equal((root.get("valor")),valor);
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
