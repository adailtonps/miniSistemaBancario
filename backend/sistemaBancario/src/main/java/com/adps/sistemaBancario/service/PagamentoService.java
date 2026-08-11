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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    private final ClienteRepository clienteRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ContaRepository contaRepository;

    public void Pagar(Cliente cliente,PagamentoResponseDTO pagamentoResponseDTO) {

        Cliente existClient = clienteRepository.findByEmail(cliente.getEmail())
                .orElseThrow(() -> new UserNaoEncontrado("Cliente não encontrado!"));

        Conta contaExist = contaRepository.findById(existClient.getConta().getId_conta())
                        .orElseThrow(() -> new UserNaoEncontrado("Conta inexistente!"));

        if(contaExist.getStatusConta()==StatusConta.DESATIVADA){
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

        if(horarioAgora.isAfter(pagamento.getDataExpiracao())){
            throw new OperacaoInvalidaException("Código expirado, pagamento cancelado!");
        }

        if(contaExist.getSaldo().compareTo(pagamento.getValorTotal()) < 0){
            throw new OperacaoInvalidaException("Saldo insuficiente!");
        }

        contaExist.setSaldo(contaExist.getSaldo().subtract(pagamento.getValorTotal()));
        pagamento.setStatusPagamento(StatusPagamento.PAGO);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        contaRepository.save(contaExist);

        RestTemplate restTemplate = new RestTemplate();
        AtualizarStatusPagamentoDTO dto = new AtualizarStatusPagamentoDTO();
        String url = "http://localhost:8080/pedidos/pagamento-confirmado";

        dto.setIdPedido(pagamento.getIdPedido());
        dto.setStatusPagamento(StatusPagamento.PAGO);

        try{restTemplate.postForEntity(
                url,
                dto,
                Void.class
        );
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 60000)
    public void pedidoExpirado() {
        List<Pagamento> pagamentoPendentes = pagamentoRepository.findByStatusPagamento(StatusPagamento.PENDENTE_PAGAMENTO);
        LocalDateTime horarioAgora = LocalDateTime.now();

        for(Pagamento pagamento : pagamentoPendentes){
            if (horarioAgora.isAfter(pagamento.getDataExpiracao())) {
                pagamento.setStatusPagamento(StatusPagamento.CANCELADO);
                pagamentoRepository.save(pagamento);
                RestTemplate restTemplate = new RestTemplate();
                AtualizarStatusPagamentoDTO dto = new AtualizarStatusPagamentoDTO();
                String url = "http://localhost:8080/pedidos/pagamento-confirmado";
                dto.setIdPedido(pagamento.getIdPedido());
                dto.setStatusPagamento(StatusPagamento.CANCELADO);
                restTemplate.postForEntity(
                        url,
                        dto,
                        Void.class
                );
            }
        }
    }
}
