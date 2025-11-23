package com.firecheck.api.dto;

import com.firecheck.api.model.Edificacao; // Importe a entidade

// Classe simples para transferir dados de Edificacao para o frontend
public class EdificacaoDTO {

    private Long idEdificacao;
    private String nome;
    private String endereco;
    private String cep;
    private Long idCliente; // Apenas o ID do cliente

    // Construtor vazio (necessário para algumas bibliotecas)
    public EdificacaoDTO() {}

    // Construtor que recebe a Entidade e copia os dados
    public EdificacaoDTO(Edificacao edificacao) {
        this.idEdificacao = edificacao.getIdEdificacao();
        this.nome = edificacao.getNome();
        this.endereco = edificacao.getEndereco();
        this.cep = edificacao.getCep();
        // Pega o ID do cliente de forma segura
        if (edificacao.getCliente() != null) {
            this.idCliente = edificacao.getCliente().getId();
        } else {
            this.idCliente = null; // Ou lançar erro se cliente for obrigatório
        }
    }

    // Getters (Setters não são estritamente necessários para DTO de resposta)
    public Long getIdEdificacao() { return idEdificacao; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getCep() { return cep; }
    public Long getIdCliente() { return idCliente; }

     // toString opcional para DTO
    @Override
    public String toString() {
         return "EdificacaoDTO [ID=" + idEdificacao + ", Nome=" + nome + ", idCliente=" + idCliente + "]";
    }
}