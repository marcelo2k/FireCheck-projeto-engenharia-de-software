package com.firecheck.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razaoSocial;
    private String cnpjCpf;
    private String endereco;
    private String telefone;
    private String responsavel;
    private String email;

    public Cliente() {}

    public Cliente(String razaoSocial, String cnpjCpf, String endereco, String telefone, String responsavel, String email) {
        this.razaoSocial = razaoSocial;
        this.cnpjCpf = cnpjCpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.responsavel = responsavel;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getCnpjCpf() { return cnpjCpf; }
    public void setCnpjCpf(String cnpjCpf) { this.cnpjCpf = cnpjCpf; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Cliente [ID=" + id + ", Razao Social=" + razaoSocial + ", CNPJ/CPF=" + cnpjCpf + "]";
    }
}