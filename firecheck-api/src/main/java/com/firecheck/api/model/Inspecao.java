package com.firecheck.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspecoes")
public class Inspecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInspecao;

    private LocalDateTime dataInspecao;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico", referencedColumnName = "id", nullable = false)
    private Usuario tecnico; // Ligação com Usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_edificacao", referencedColumnName = "idEdificacao", nullable = false)
    private Edificacao edificacao; // Ligação com Edificacao

    public Inspecao() {}

    // Getters e Setters (ajustados para Usuario e Edificacao)
    public Long getIdInspecao() { return idInspecao; }
    public void setIdInspecao(Long idInspecao) { this.idInspecao = idInspecao; }
    public LocalDateTime getDataInspecao() { return dataInspecao; }
    public void setDataInspecao(LocalDateTime dataInspecao) { this.dataInspecao = dataInspecao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }
    public Edificacao getEdificacao() { return edificacao; }
    public void setEdificacao(Edificacao edificacao) { this.edificacao = edificacao; }

    @Override
    public String toString() {
        Long tecnicoId = (tecnico != null) ? tecnico.getId() : null;
        Long edificacaoId = (edificacao != null) ? edificacao.getIdEdificacao() : null;
        return "Inspecao [ID=" + idInspecao + ", Data=" + dataInspecao + ", Status=" + status + ", idTecnico=" + tecnicoId + ", idEdificacao=" + edificacaoId + "]";
    }
}