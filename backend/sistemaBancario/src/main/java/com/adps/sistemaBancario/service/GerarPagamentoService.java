package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Pagamento;
import com.adps.sistemaBancario.domain.StatusPagamento;
import com.adps.sistemaBancario.dto.CriarPagamentoDTO;
import com.adps.sistemaBancario.dto.PagamentoResponseDTO;
import com.adps.sistemaBancario.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GerarPagamentoService {
    private PagamentoRepository pagamentoRepository;

    public PagamentoResponseDTO criarPagamento(CriarPagamentoDTO criarPagamentoDTO) {

        Pagamento pagamento = new Pagamento();

        LocalDateTime dataHoraPagamento = LocalDateTime.now();

        pagamento.setIdPedido(criarPagamentoDTO.getIdPedido());
        pagamento.setValorTotal(criarPagamentoDTO.getValorTotal());

        pagamento.setStatusPagamento(StatusPagamento.PENDENTE_PAGAMENTO);
        pagamento.setDataCriacao(dataHoraPagamento);
        pagamento.setDataExpiracao(dataHoraPagamento.plusMinutes(30));


        pagamento.gerarCodigoPagamento();

        pagamentoRepository.save(pagamento);

        PagamentoResponseDTO response = new PagamentoResponseDTO();

        response.setCodigoPagamento(pagamento.getCodigoPagamento());

        return response;
    }
}
