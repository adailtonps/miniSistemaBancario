package com.adps.sistemaBancario.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {
    @Id
    private String codigoPagamento;
    public void gerarCodigoPagamento(){
        if(this.codigoPagamento == null){
            this.codigoPagamento = "PAY-"+
                    UUID.randomUUID()
                            .toString()
                            .replace("-","")
                            .toUpperCase()
                            .substring(0, 14);

        }
    }

    private String idPedido;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataPagamento;

    private LocalDateTime dataExpiracao;

    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;
}
