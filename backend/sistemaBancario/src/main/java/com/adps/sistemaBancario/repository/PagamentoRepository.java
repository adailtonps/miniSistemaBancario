package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, String> {
    Optional<Pagamento> findByCodigoPagamento(String codigoPagamento);
}
