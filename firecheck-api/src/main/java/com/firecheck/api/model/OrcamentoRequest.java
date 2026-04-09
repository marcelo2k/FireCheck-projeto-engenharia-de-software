package com.firecheck.api.model;

import java.time.LocalDate;
import java.util.List;

public class OrcamentoRequest {
    private Long idEdificacao;
    private Long idUsuario;
    private LocalDate dataValidade;
    private List<ItemOrcamentoRequest> itens;

    public Long getIdEdificacao() { return idEdificacao; }
    public Long getIdUsuario() { return idUsuario; }
    public LocalDate getDataValidade() { return dataValidade; }
    public List<ItemOrcamentoRequest> getItens() { return itens; }
}