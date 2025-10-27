package model;
public class Cliente {

    private String razaoSocial;
    private String cnpjCpf;
    private String endereco;
    private String telefone;
    private String responsavel;
    private String email;

    public Cliente(String razaoSocial, String cnpjCpf, String endereco, String telefone, String responsavel, String email) {
        this.razaoSocial = razaoSocial;
        this.cnpjCpf = cnpjCpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.responsavel = responsavel;
        this.email = email;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }
    

    @Override
    public String toString() {
        return "Cliente [Razao Social=" + razaoSocial + ", CNPJ/CPF=" + cnpjCpf + ", Responsavel=" + responsavel + "]";
    }
}
