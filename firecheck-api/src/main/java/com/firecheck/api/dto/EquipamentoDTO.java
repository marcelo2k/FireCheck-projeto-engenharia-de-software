package com.firecheck.api.dto;

import com.firecheck.api.model.Equipamento;
import java.time.LocalDate;

public class EquipamentoDTO {
    private Long idEquipamento;
    private String tipoEquipamento;
    private String localizacao;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private Long idEdificacao;

    public EquipamentoDTO(Equipamento e) {
        this.idEquipamento = e.getIdEquipamento();
        this.tipoEquipamento = e.getTipoEquipamento();
        this.localizacao = e.getLocalizacao();
        this.dataFabricacao = e.getDataFabricacao();
        this.dataValidade = e.getDataValidade();
        this.idEdificacao = (e.getEdificacao() != null) ? e.getEdificacao().getIdEdificacao() : null;
    }
    // Getters
    public Long getIdEquipamento() { return idEquipamento; }
    public String getTipoEquipamento() { return tipoEquipamento; }
    public String getLocalizacao() { return localizacao; }
    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public Long getIdEdificacao() { return idEdificacao; }

    @Override
    public String toString() {
         return "Equipamento [ID=" + idEquipamento + ", Tipo=" + tipoEquipamento + ", Local=" + localizacao + ", Vence=" + dataValidade + ", idEdificacao=" + idEdificacao + "]";
    }
}