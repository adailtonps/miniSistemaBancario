package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, String> {
    Optional<Pagamento> findByCodigoPagamento(String codigoPagamento);
}
