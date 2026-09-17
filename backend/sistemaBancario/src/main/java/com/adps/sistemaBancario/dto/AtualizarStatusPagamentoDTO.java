package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusPagamentoDTO {
    private String idPedido;
    private StatusPagamento statusPagamento;
    private LocalDateTime dataPagamento;
    private Long id_do_pagador;
    private String nome_do_pagador;

    public void getId_do_pagador(Long id) {
    }
}
