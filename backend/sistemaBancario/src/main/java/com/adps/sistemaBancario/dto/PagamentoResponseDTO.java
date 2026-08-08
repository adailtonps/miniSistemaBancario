package com.adps.sistemaBancario.dto;

import com.adps.sistemaBancario.domain.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoResponseDTO {
    private String codigoPagamento;
}
