package com.firecheck.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_inspecionados")
public class ItemInspecionado {

    @EmbeddedId // Usa a chave composta definida na classe ItemInspecionadoId
    private ItemInspecionadoId id;

    // Mapeia as colunas da chave composta para os relacionamentos ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idInspecao") // Mapeia o campo idInspecao da classe ItemInspecionadoId
    @JoinColumn(name = "id_inspecao", referencedColumnName = "idInspecao")
    private Inspecao inspecao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idEquipamento") // Mapeia o campo idEquipamento da classe ItemInspecionadoId
    @JoinColumn(name = "id_equipamento", referencedColumnName = "idEquipamento")
    private Equipamento equipamento;

    private String statusGeral;
    @Lob // Para campos de texto potencialmente longos
    private String observacoes;

    public ItemInspecionado() {}

    // Getters e Setters
    public ItemInspecionadoId getId() { return id; }
    public void setId(ItemInspecionadoId id) { this.id = id; }
    public Inspecao getInspecao() { return inspecao; }
    public void setInspecao(Inspecao inspecao) { this.inspecao = inspecao; }
    public Equipamento getEquipamento() { return equipamento; }
    public void setEquipamento(Equipamento equipamento) { this.equipamento = equipamento; }
    public String getStatusGeral() { return statusGeral; }
    public void setStatusGeral(String statusGeral) { this.statusGeral = statusGeral; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        Long inspecaoId = (id != null) ? id.getIdInspecao() : null;
        Long equipamentoId = (id != null) ? id.getIdEquipamento() : null;
        return "ItemInspecionado [idInspecao=" + inspecaoId + ", idEquipamento=" + equipamentoId + ", Status=" + statusGeral + "]";
    }
}