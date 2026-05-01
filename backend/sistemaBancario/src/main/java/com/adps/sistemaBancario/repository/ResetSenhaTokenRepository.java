package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.ResetSenhaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ResetSenhaTokenRepository extends JpaRepository<ResetSenhaToken, Long> {
    Optional<ResetSenhaToken> findByToken (String token);
    Optional<ResetSenhaToken> findByCliente (Cliente cliente);
    @Modifying
    @Transactional
    @Query("DELETE FROM ResetSenhaToken r WHERE r.cliente = :cliente")
    void deleteByCliente(@Param("cliente") Cliente cliente);
    }

