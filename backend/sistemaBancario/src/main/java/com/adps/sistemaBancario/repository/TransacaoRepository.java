package com.adps.sistemaBancario.repository;

import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.domain.Transacao;
import com.adps.sistemaBancario.domain.TransacaoTipo;
import com.adps.sistemaBancario.dto.HistoricoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer>, JpaSpecificationExecutor<Transacao> {
    List<Transacao> findByContaOrderByDataHoraTransacaoDesc(Conta conta);
    void deleteByConta(Conta conta);

}
