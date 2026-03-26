package com.firecheck.api.dto;

import com.firecheck.api.model.ItemInspecionado;

public class ItemInspecionadoDTO {
    private Long idInspecao;
    private Long idEquipamento;
    private String tipoEquipamento; // Adicionado
    private String statusGeral;
    private String observacoes;

    public ItemInspecionadoDTO(ItemInspecionado item) {
        this.idInspecao = item.getId().getIdInspecao();
        this.idEquipamento = item.getId().getIdEquipamento();
        this.tipoEquipamento = (item.getEquipamento() != null) ? item.getEquipamento().getTipoEquipamento() : "N/A";
        this.statusGeral = item.getStatusGeral();
        this.observacoes = item.getObservacoes();
    }
    // Getters
    public Long getIdInspecao() { return idInspecao; }
    public Long getIdEquipamento() { return idEquipamento; }
    public String getTipoEquipamento() { return tipoEquipamento; }
    public String getStatusGeral() { return statusGeral; }
    public String getObservacoes() { return observacoes; }

     @Override
    public String toString() {
        return "Item [Equip ID=" + idEquipamento + " ("+ tipoEquipamento +"), Status=" + statusGeral + ", Obs=" + observacoes + "]";
    }
}