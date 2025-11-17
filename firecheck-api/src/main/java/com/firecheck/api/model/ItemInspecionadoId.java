package com.firecheck.api.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable // Marca esta classe como "embutível" (será parte de outra entidade)
public class ItemInspecionadoId implements Serializable {

    private Long idInspecao;
    private Long idEquipamento;

    public ItemInspecionadoId() {}

    public ItemInspecionadoId(Long idInspecao, Long idEquipamento) {
        this.idInspecao = idInspecao;
        this.idEquipamento = idEquipamento;
    }

    // Getters, Setters, hashCode e equals são OBRIGATÓRIOS para chaves compostas
    public Long getIdInspecao() { return idInspecao; }
    public void setIdInspecao(Long idInspecao) { this.idInspecao = idInspecao; }
    public Long getIdEquipamento() { return idEquipamento; }
    public void setIdEquipamento(Long idEquipamento) { this.idEquipamento = idEquipamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemInspecionadoId that = (ItemInspecionadoId) o;
        return Objects.equals(idInspecao, that.idInspecao) && Objects.equals(idEquipamento, that.idEquipamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInspecao, idEquipamento);
    }
}