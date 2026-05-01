package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.ResetSenhaToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetSenhaTokenRepository extends JpaRepository<ResetSenhaToken, Long> {
    Optional<ResetSenhaToken> findByToken (String token);
    Optional<ResetSenhaToken> findByCliente (Cliente cliente);
    void deleteByCliente(Cliente cliente);
}
