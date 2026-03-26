package com.firecheck.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_orcamento")
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemOrcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orcamento", referencedColumnName = "idOrcamento", nullable = false)
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servico", referencedColumnName = "idServico", nullable = false)
    private Servico servico;

    private Integer quantidade;
    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public ItemOrcamento() {}

    // Getters e Setters (ajustados)
    public Long getIdItemOrcamento() { return idItemOrcamento; }
    public void setIdItemOrcamento(Long idItemOrcamento) { this.idItemOrcamento = idItemOrcamento; }
    public Orcamento getOrcamento() { return orcamento; }
    public void setOrcamento(Orcamento orcamento) { this.orcamento = orcamento; }
    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}