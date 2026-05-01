package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.StatusConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByClienteAndStatusConta(Cliente cliente, StatusConta statusConta);
    Optional<Conta> findByCliente(Cliente cliente);
}

