package com.firecheck.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdemServico;

    private LocalDate dataCriacao;
    private LocalDate dataExecucaoPrevista;
    private String statusServico;

    // Relacionamento OneToOne: Uma OS está ligada a um único Orçamento
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orcamento", referencedColumnName = "idOrcamento", nullable = false, unique = true)
    private Orcamento orcamento;

    // Construtor padrão agora inicializa no Service
    public OrdemServico() {}

    // Getters e Setters (ajustados)
    public Long getIdOrdemServico() { return idOrdemServico; }
    public void setIdOrdemServico(Long idOrdemServico) { this.idOrdemServico = idOrdemServico; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDate getDataExecucaoPrevista() { return dataExecucaoPrevista; }
    public void setDataExecucaoPrevista(LocalDate dataExecucaoPrevista) { this.dataExecucaoPrevista = dataExecucaoPrevista; }
    public String getStatusServico() { return statusServico; }
    public void setStatusServico(String statusServico) { this.statusServico = statusServico; }
    public Orcamento getOrcamento() { return orcamento; }
    public void setOrcamento(Orcamento orcamento) { this.orcamento = orcamento; }

    @Override
    public String toString() {
        Long orcamentoId = (orcamento != null) ? orcamento.getIdOrcamento() : null;
        return "OrdemServico [ID=" + idOrdemServico + ", OrcamentoID=" + orcamentoId + ", Status=" + statusServico + ", PrevistoPara=" + dataExecucaoPrevista + "]";
    }
}