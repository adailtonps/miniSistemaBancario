package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusPagamentoDTO {
    private String idPedido;
    private StatusPagamento statusPagamento;
}
