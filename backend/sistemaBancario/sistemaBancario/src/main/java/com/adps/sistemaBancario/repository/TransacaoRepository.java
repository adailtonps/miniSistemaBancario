package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByContaOrderByDataHoraTransacaoDesc(Conta conta);
    void deleteByConta(Conta conta);
}
