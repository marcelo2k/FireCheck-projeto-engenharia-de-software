package com.firecheck.api.dto;

import com.firecheck.api.model.Inspecao;
import java.time.LocalDateTime;

public class InspecaoDTO {
    private Long idInspecao;
    private LocalDateTime dataInspecao;
    private String status;
    private Long idTecnico;
    private String nomeTecnico; // Adicionado para exibição
    private Long idEdificacao;
    private String nomeEdificacao; // Adicionado para exibição

    public InspecaoDTO(Inspecao i) {
        this.idInspecao = i.getIdInspecao();
        this.dataInspecao = i.getDataInspecao();
        this.status = i.getStatus();
        this.idTecnico = (i.getTecnico() != null) ? i.getTecnico().getId() : null;
        this.nomeTecnico = (i.getTecnico() != null) ? i.getTecnico().getNomeCompleto() : "N/A";
        this.idEdificacao = (i.getEdificacao() != null) ? i.getEdificacao().getIdEdificacao() : null;
        this.nomeEdificacao = (i.getEdificacao() != null) ? i.getEdificacao().getNome() : "N/A";
    }
    // Getters
    public Long getIdInspecao() { return idInspecao; }
    public LocalDateTime getDataInspecao() { return dataInspecao; }
    public String getStatus() { return status; }
    public Long getIdTecnico() { return idTecnico; }
    public String getNomeTecnico() { return nomeTecnico; }
    public Long getIdEdificacao() { return idEdificacao; }
    public String getNomeEdificacao() { return nomeEdificacao; }

    @Override
    public String toString() {
         return "Inspecao [ID=" + idInspecao + ", Data=" + dataInspecao + ", Status=" + status + ", Tecnico=" + nomeTecnico + ", Edificacao=" + nomeEdificacao + "]";
    }
}