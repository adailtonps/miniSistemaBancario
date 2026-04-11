package com.adps.sistemaBancario.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import jakarta.persistence.*;


@Entity
@Getter
@Table(name = "cliente")
public class Cliente {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_cliente")
        private Long id;

        private String nome;

        @OneToOne(mappedBy = "cliente")
        @JsonIgnore
        private Conta conta;

        @Column(unique = true, nullable = false)
        private String email;

        @JsonIgnore
        private String senhaCliente;


    public Cliente(){};

    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.senhaCliente = senhaCliente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenhaCliente() {
        return senhaCliente;
    }

    public void setSenhaCliente(String senhaCliente) {
        this.senhaCliente = senhaCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void alterarEmail(String email) {
        this.email = email;
    }

}
