package com.firecheck.api.dto;

import com.firecheck.api.model.ItemOrcamento;
import java.math.BigDecimal;

public class ItemOrcamentoDTO {
    private Long idItemOrcamento;
    private Long idOrcamento;
    private Long idServico;
    private String nomeServico; // Adicionado
    private Integer quantidade;
    private BigDecimal valorUnitario;

    public ItemOrcamentoDTO(ItemOrcamento item) {
        this.idItemOrcamento = item.getIdItemOrcamento();
        this.idOrcamento = (item.getOrcamento() != null) ? item.getOrcamento().getIdOrcamento() : null;
        this.idServico = (item.getServico() != null) ? item.getServico().getIdServico() : null;
        this.nomeServico = (item.getServico() != null) ? item.getServico().getNome() : "N/A";
        this.quantidade = item.getQuantidade();
        this.valorUnitario = item.getValorUnitario();
    }
    // Getters
    public Long getIdItemOrcamento() { return idItemOrcamento; }
    public Long getIdOrcamento() { return idOrcamento; }
    public Long getIdServico() { return idServico; }
    public String getNomeServico() { return nomeServico; }
    public Integer getQuantidade() { return quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
}