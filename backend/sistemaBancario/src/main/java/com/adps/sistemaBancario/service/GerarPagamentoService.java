package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Pagamento;
import com.adps.sistemaBancario.domain.StatusPagamento;
import com.adps.sistemaBancario.dto.CriarPagamentoDTO;
import com.adps.sistemaBancario.dto.PagamentoResponseDTO;
import com.adps.sistemaBancario.exception.NegocioException;
import com.adps.sistemaBancario.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GerarPagamentoService {
    private final PagamentoRepository pagamentoRepository;

    public PagamentoResponseDTO criarPagamento(CriarPagamentoDTO criarPagamentoDTO) {

        Pagamento pagamento = new Pagamento();

        LocalDateTime dataHoraPagamento = LocalDateTime.now();

        pagamento.setIdPedido(criarPagamentoDTO.getIdPedido());
        pagamento.setValorTotal(criarPagamentoDTO.getValorTotal());

        if(criarPagamentoDTO.getIdPedido() == null || criarPagamentoDTO.getValorTotal() == null){
            throw new NegocioException("Erro ao criar Pagamento: Id e valor total do pedido são nulls!");
        }

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
