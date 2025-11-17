package com.firecheck.api.dto;

import com.firecheck.api.model.NaoConformidade;

public class NaoConformidadeDTO {
    private Long idNaoConformidade;
    private String descricao;
    private String fotoUrl;
    private Long idInspecao;
    private Long idEquipamento;
    private String tipoEquipamento; // Adicionado

    public NaoConformidadeDTO(NaoConformidade nc) {
        this.idNaoConformidade = nc.getIdNaoConformidade();
        this.descricao = nc.getDescricao();
        this.fotoUrl = nc.getFotoUrl();
        this.idInspecao = (nc.getItemInspecionado() != null) ? nc.getItemInspecionado().getId().getIdInspecao() : null;
        this.idEquipamento = (nc.getItemInspecionado() != null) ? nc.getItemInspecionado().getId().getIdEquipamento() : null;
        this.tipoEquipamento = (nc.getItemInspecionado() != null && nc.getItemInspecionado().getEquipamento() != null)
                              ? nc.getItemInspecionado().getEquipamento().getTipoEquipamento() : "N/A";
    }
    // Getters
    public Long getIdNaoConformidade() { return idNaoConformidade; }
    public String getDescricao() { return descricao; }
    public String getFotoUrl() { return fotoUrl; }
    public Long getIdInspecao() { return idInspecao; }
    public Long getIdEquipamento() { return idEquipamento; }
    public String getTipoEquipamento() { return tipoEquipamento; }

    @Override
    public String toString() {
        return "NC [ID=" + idNaoConformidade + ", EquipID=" + idEquipamento + " ("+ tipoEquipamento +"), Desc=" + descricao + "]";
    }
}