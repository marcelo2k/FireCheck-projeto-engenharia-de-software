package com.firecheck.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "edificacoes")
public class Edificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEdificacao;

    private String nome;
    private String endereco;
    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
    private Cliente cliente;

    public Edificacao() {}

    public Edificacao(String nome, String endereco, String cep, Cliente cliente) {
        this.nome = nome;
        this.endereco = endereco;
        this.cep = cep;
        this.cliente = cliente;
    }

    public Long getIdEdificacao() { return idEdificacao; }
    public void setIdEdificacao(Long idEdificacao) { this.idEdificacao = idEdificacao; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public String toString() {
        Long clienteId = (cliente != null) ? cliente.getId() : null;
        return "Edificacao [ID=" + idEdificacao + ", Nome=" + nome + ", idCliente=" + clienteId + "]";
    }
}