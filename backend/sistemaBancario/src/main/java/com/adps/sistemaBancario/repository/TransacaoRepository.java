package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByContaOrderByDataHoraTransacaoDesc(Conta conta);
    void deleteByConta(Conta conta);
}
