package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.Transacao;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByContaOrderByDataHoraTransacaoDesc(Conta conta);
    void deleteByConta(Conta conta);
}
