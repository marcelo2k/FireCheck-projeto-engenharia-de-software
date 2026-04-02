package com.firecheck.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nao_conformidades")
public class NaoConformidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNaoConformidade;

    @Lob
    private String descricao;
    private String fotoUrl;

    // Relacionamento com ItemInspecionado (chave estrangeira composta)
    // Usamos @ManyToOne aqui, assumindo que uma NC pertence a UM item específico.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({ // Define as colunas FK compostas na tabela nao_conformidades
        @JoinColumn(name = "id_inspecao", referencedColumnName = "id_inspecao"),
        @JoinColumn(name = "id_equipamento", referencedColumnName = "id_equipamento")
    })
    private ItemInspecionado itemInspecionado;

    public NaoConformidade() {}

    // Getters e Setters
    public Long getIdNaoConformidade() { return idNaoConformidade; }
    public void setIdNaoConformidade(Long idNaoConformidade) { this.idNaoConformidade = idNaoConformidade; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public ItemInspecionado getItemInspecionado() { return itemInspecionado; }
    public void setItemInspecionado(ItemInspecionado itemInspecionado) { this.itemInspecionado = itemInspecionado; }

    @Override
    public String toString() {
        Long inspecaoId = (itemInspecionado != null && itemInspecionado.getId() != null) ? itemInspecionado.getId().getIdInspecao() : null;
        Long equipamentoId = (itemInspecionado != null && itemInspecionado.getId() != null) ? itemInspecionado.getId().getIdEquipamento() : null;
        return "NaoConformidade [ID=" + idNaoConformidade + ", Descricao=" + descricao + ", InspecaoID=" + inspecaoId + ", EquipID=" + equipamentoId + "]";
    }
}