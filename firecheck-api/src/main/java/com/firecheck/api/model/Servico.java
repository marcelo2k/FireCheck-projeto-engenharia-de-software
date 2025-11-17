package com.firecheck.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServico;

    @Column(nullable = false, unique = true) // Nome é obrigatório e único
    private String nome;
    private String descricao;
    private Double tempoExecucaoHoras;
    @Column(precision = 10, scale = 2) // Define precisão para valores monetários
    private BigDecimal valorUnitario;
    private Integer estoque;

    public Servico() {}

    // Getters e Setters (sem alterações)
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

    @Override
    public String toString() {
        return "Servico [ID=" + idServico + ", Nome=" + nome + ", Valor=R$ " + valorUnitario + ", Estoque=" + estoque + "]";
    }
}