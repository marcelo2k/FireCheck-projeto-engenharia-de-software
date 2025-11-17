package com.firecheck.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "equipamentos")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipamento;

    private String tipoEquipamento;
    private String localizacao;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_edificacao", referencedColumnName = "idEdificacao", nullable = false)
    private Edificacao edificacao;

    public Equipamento() {}

    // Getters e Setters (ajustados para Edificacao)
    public Long getIdEquipamento() { return idEquipamento; }
    public void setIdEquipamento(Long idEquipamento) { this.idEquipamento = idEquipamento; }
    public String getTipoEquipamento() { return tipoEquipamento; }
    public void setTipoEquipamento(String tipoEquipamento) { this.tipoEquipamento = tipoEquipamento; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public void setDataFabricacao(LocalDate dataFabricacao) { this.dataFabricacao = dataFabricacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
    public Edificacao getEdificacao() { return edificacao; }
    public void setEdificacao(Edificacao edificacao) { this.edificacao = edificacao; }

    @Override
    public String toString() {
        Long edificacaoId = (edificacao != null) ? edificacao.getIdEdificacao() : null;
        return "Equipamento [ID=" + idEquipamento + ", Tipo=" + tipoEquipamento + ", Local=" + localizacao + ", Vence=" + dataValidade + ", idEdificacao=" + edificacaoId + "]";
    }
}