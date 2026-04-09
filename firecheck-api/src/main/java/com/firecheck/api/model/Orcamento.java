package com.firecheck.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrcamento;

    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    private Usuario usuario; // Ligação com Usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_edificacao", referencedColumnName = "idEdificacao", nullable = false)
    private Edificacao edificacao; // Ligação com Edificacao

    // Construtor padrão agora inicializa no Service
    public Orcamento() {}

    // Getters e Setters (ajustados)
    public Long getIdOrcamento() { return idOrcamento; }
    public void setIdOrcamento(Long idOrcamento) { this.idOrcamento = idOrcamento; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Edificacao getEdificacao() { return edificacao; }
    public void setEdificacao(Edificacao edificacao) { this.edificacao = edificacao; }

    @Override
    public String toString() {
        Long edificacaoId = (edificacao != null) ? edificacao.getIdEdificacao() : null;
        return "Orcamento [ID=" + idOrcamento + ", Status=" + status + ", Valor=R$ " + valorTotal + ", EdificacaoID=" + edificacaoId + "]";
    }
}