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

        System.out.println(">>> 1 - ENTROU NO SERVICE");

        Pagamento pagamento = new Pagamento();

        LocalDateTime dataHoraPagamento = LocalDateTime.now();

        pagamento.setIdPedido(criarPagamentoDTO.getIdPedido());
        pagamento.setValorTotal(criarPagamentoDTO.getValorTotal());

        System.out.println(">>> 2 - DADOS DO PAGAMENTO DEFINIDOS");

        pagamento.setStatusPagamento(StatusPagamento.PENDENTE_PAGAMENTO);
        pagamento.setDataCriacao(dataHoraPagamento);
        pagamento.setDataExpiracao(dataHoraPagamento.plusMinutes(30));

        System.out.println(">>> 3 - ANTES DE GERAR CÓDIGO");

        pagamento.gerarCodigoPagamento();

        System.out.println(">>> 4 - CÓDIGO GERADO");

        pagamentoRepository.save(pagamento);

        System.out.println(">>> 5 - PAGAMENTO SALVO");

        PagamentoResponseDTO response = new PagamentoResponseDTO();

        response.setCodigoPagamento(pagamento.getCodigoPagamento());

        System.out.println(">>> 6 - RESPONSE CRIADO");

        return response;
    }
}
