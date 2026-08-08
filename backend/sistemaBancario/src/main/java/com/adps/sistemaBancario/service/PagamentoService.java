package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.*;
import com.adps.sistemaBancario.dto.AtualizarStatusPagamentoDTO;
import com.adps.sistemaBancario.dto.PagamentoResponseDTO;
import com.adps.sistemaBancario.exception.OperacaoInvalidaException;
import com.adps.sistemaBancario.exception.UserNaoEncontrado;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.repository.ContaRepository;
import com.adps.sistemaBancario.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    private ClienteRepository clienteRepository;
    private Conta conta;
    private PagamentoRepository pagamentoRepository;
    private ContaRepository contaRepository;

    public void Pagar(Cliente cliente,PagamentoResponseDTO pagamentoResponseDTO) {
        Cliente existClient = clienteRepository.findByEmail(cliente.getEmail())
                .orElseThrow(() -> new UserNaoEncontrado("Cliente não encontrado!"));

        if(conta.getStatusConta()==StatusConta.DESATIVADA){
            throw new OperacaoInvalidaException("A conta está desativada!");
        }
        if(!pagamentoResponseDTO.getCodigoPagamento().startsWith("PAY-") ||
            pagamentoResponseDTO.getCodigoPagamento().length() != 18){
            throw new OperacaoInvalidaException("Código de pagamento inválido!");
        }

        Pagamento pagamento = pagamentoRepository.findByCodigoPagamento(
                pagamentoResponseDTO.getCodigoPagamento()).orElseThrow(()
                -> new OperacaoInvalidaException("Código de pagamento inválido!"));

        if(pagamento.getStatusPagamento() == StatusPagamento.PAGO){
            throw new OperacaoInvalidaException("Esse pedido já foi pago!");
        }
        LocalDateTime horarioAgora = LocalDateTime.now();
        RestTemplate restTemplate = new RestTemplate();
        AtualizarStatusPagamentoDTO dto = new AtualizarStatusPagamentoDTO();
        String url = "http://localhost:8080/pedidos/pagamento-confirmado";

        if(horarioAgora.isAfter(pagamento.getDataExpiracao())){
            pagamento.setStatusPagamento(StatusPagamento.CANCELADO);
            pagamentoRepository.save(pagamento);
            dto.setIdPedido(pagamento.getIdPedido());
            dto.setStatusPagamento(StatusPagamento.CANCELADO);
            restTemplate.postForEntity(
                    url,
                    dto,
                    Void.class
            );
            throw new OperacaoInvalidaException("Código expirado, pagamento cancelado!");
        }

        if(conta.getSaldo().compareTo(pagamento.getValorTotal()) < 0){
            throw new OperacaoInvalidaException("Saldo insuficiente!");
        }

        conta.setSaldo(conta.getSaldo().subtract(pagamento.getValorTotal()));
        pagamento.setStatusPagamento(StatusPagamento.PAGO);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        contaRepository.save(conta);

        dto.setStatusPagamento(StatusPagamento.PAGO);
        dto.setIdPedido(pagamento.getIdPedido());

        restTemplate.postForEntity(
                url,
                dto,
                Void.class
        );
    }

    public void pedidoExpirado(){

    }
}
