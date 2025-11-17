package com.firecheck.api.dto;

import com.firecheck.api.model.Servico;
import java.math.BigDecimal;

public class ServicoDTO {
    private Long idServico;
    private String nome;
    private String descricao;
    private Double tempoExecucaoHoras;
    private BigDecimal valorUnitario;
    private Integer estoque;

    public ServicoDTO(Servico s) {
        this.idServico = s.getIdServico();
        this.nome = s.getNome();
        this.descricao = s.getDescricao();
        this.tempoExecucaoHoras = s.getTempoExecucaoHoras();
        this.valorUnitario = s.getValorUnitario();
        this.estoque = s.getEstoque();
    }
    // Getters
    public Long getIdServico() { return idServico; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Double getTempoExecucaoHoras() { return tempoExecucaoHoras; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public Integer getEstoque() { return estoque; }

    @Override
    public String toString() {
         return "Servico [ID=" + idServico + ", Nome=" + nome + ", Valor=R$ " + valorUnitario + ", Estoque=" + (estoque != null ? estoque : "N/A") + "]";
    }
}