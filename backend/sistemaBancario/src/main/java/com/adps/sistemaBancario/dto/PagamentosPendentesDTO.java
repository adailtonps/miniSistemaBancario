package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagamentosPendentesDTO {
    private String idUsuario;
    private String idPagamento;
    private StatusPagamento statusPagamento;
}
