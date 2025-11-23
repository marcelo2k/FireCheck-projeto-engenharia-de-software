package com.firecheck.api.dto;

import com.firecheck.api.model.OrdemServico;
import java.time.LocalDate;

public class OrdemServicoDTO {
    private Long idOrdemServico;
    private LocalDate dataCriacao;
    private LocalDate dataExecucaoPrevista;
    private String statusServico;
    private Long idOrcamento;
    private String nomeEdificacaoOrcamento; // Adicionado

    public OrdemServicoDTO(OrdemServico os) {
        this.idOrdemServico = os.getIdOrdemServico();
        this.dataCriacao = os.getDataCriacao();
        this.dataExecucaoPrevista = os.getDataExecucaoPrevista();
        this.statusServico = os.getStatusServico();
        this.idOrcamento = (os.getOrcamento() != null) ? os.getOrcamento().getIdOrcamento() : null;
        this.nomeEdificacaoOrcamento = (os.getOrcamento() != null && os.getOrcamento().getEdificacao() != null)
                                      ? os.getOrcamento().getEdificacao().getNome() : "N/A";
    }
    // Getters
    public Long getIdOrdemServico() { return idOrdemServico; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public LocalDate getDataExecucaoPrevista() { return dataExecucaoPrevista; }
    public String getStatusServico() { return statusServico; }
    public Long getIdOrcamento() { return idOrcamento; }
    public String getNomeEdificacaoOrcamento() { return nomeEdificacaoOrcamento; }

    @Override
    public String toString() {
         return "OS [ID=" + idOrdemServico + ", OrcID=" + idOrcamento + " ("+ nomeEdificacaoOrcamento +"), Status=" + statusServico + ", Previsto=" + dataExecucaoPrevista + "]";
    }
}