package com.firecheck.api.dto;

import com.firecheck.api.model.Orcamento;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OrcamentoDTO {
    private Long idOrcamento;
    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    private BigDecimal valorTotal;
    private String status;
    private Long idUsuario;
    private String nomeUsuario; // Adicionado
    private Long idEdificacao;
    private String nomeEdificacao; // Adicionado

    public OrcamentoDTO(Orcamento o) {
        this.idOrcamento = o.getIdOrcamento();
        this.dataCriacao = o.getDataCriacao();
        this.dataValidade = o.getDataValidade();
        this.valorTotal = o.getValorTotal();
        this.status = o.getStatus();
        this.idUsuario = (o.getUsuario() != null) ? o.getUsuario().getId() : null;
        this.nomeUsuario = (o.getUsuario() != null) ? o.getUsuario().getNomeCompleto() : "N/A";
        this.idEdificacao = (o.getEdificacao() != null) ? o.getEdificacao().getIdEdificacao() : null;
        this.nomeEdificacao = (o.getEdificacao() != null) ? o.getEdificacao().getNome() : "N/A";
    }
    // Getters
    public Long getIdOrcamento() { return idOrcamento; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getStatus() { return status; }
    public Long getIdUsuario() { return idUsuario; }
    public String getNomeUsuario() { return nomeUsuario; }
    public Long getIdEdificacao() { return idEdificacao; }
    public String getNomeEdificacao() { return nomeEdificacao; }

    @Override
    public String toString() {
        return "Orcamento [ID=" + idOrcamento + ", Status=" + status + ", Valor=R$ " + valorTotal + ", Edificacao=" + nomeEdificacao + "]";
    }
}