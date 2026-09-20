package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Pagamento;
import com.adps.sistemaBancario.domain.StatusPagamento;
import com.adps.sistemaBancario.domain.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, String>, JpaSpecificationExecutor<Pagamento> {
    Optional<Pagamento> findByCodigoPagamento(String codigoPagamento);
    List<Pagamento> findByStatusPagamento(StatusPagamento statusPagamento);
    List<Pagamento> findByClienteOrderByDataPagamentoDesc(Cliente cliente);

}
