package com.firecheck.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServico;

    @Column(nullable = false, unique = true)
    private String nome;
    private String descricao;
    private Double tempoExecucaoHoras;
    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitario;
    private Integer estoque;
    private Integer estoqueMinimo; // NOVO CAMPO

    public Servico() {}

    // Getters e Setters
    public Long getIdServico() { return idServico; }
    public void setIdServico(Long idServico) { this.idServico = idServico; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getTempoExecucaoHoras() { return tempoExecucaoHoras; }
    public void setTempoExecucaoHoras(Double tempoExecucaoHoras) { this.tempoExecucaoHoras = tempoExecucaoHoras; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    @Override
    public String toString() {
        return "Servico [ID=" + idServico + ", Nome=" + nome + ", Estoque=" + estoque + ", Mín=" + estoqueMinimo + "]";
    }
}