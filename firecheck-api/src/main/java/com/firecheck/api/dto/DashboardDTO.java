package com.firecheck.api.dto;

public class DashboardDTO {
    private long totalClientes;
    private long totalTecnicos;
    private long totalEdificacoes;
    private long totalEquipamentos;
    private long orcamentosPendentes;
    private long osEmAberto;
    private long alertasEstoque;

    public DashboardDTO(long totalClientes, long totalTecnicos, long totalEdificacoes, long totalEquipamentos, long orcamentosPendentes, long osEmAberto, long alertasEstoque) {
        this.totalClientes = totalClientes;
        this.totalTecnicos = totalTecnicos;
        this.totalEdificacoes = totalEdificacoes;
        this.totalEquipamentos = totalEquipamentos;
        this.orcamentosPendentes = orcamentosPendentes;
        this.osEmAberto = osEmAberto;
        this.alertasEstoque = alertasEstoque;
    }

    // Getters são obrigatórios para o JSON funcionar
    public long getTotalClientes() { return totalClientes; }
    public long getTotalTecnicos() { return totalTecnicos; }
    public long getTotalEdificacoes() { return totalEdificacoes; }
    public long getTotalEquipamentos() { return totalEquipamentos; }
    public long getOrcamentosPendentes() { return orcamentosPendentes; }
    public long getOsEmAberto() { return osEmAberto; }
    public long getAlertasEstoque() { return alertasEstoque; }
}