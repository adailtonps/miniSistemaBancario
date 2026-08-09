package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.ResetSenhaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetSenhaTokenRepository extends JpaRepository<ResetSenhaToken, Long> {
    Optional<ResetSenhaToken> findByToken (String token);
    Optional<ResetSenhaToken> findByCliente (Cliente cliente);
}
