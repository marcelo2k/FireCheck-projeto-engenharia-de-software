package service;

import model.Cliente;
import repository.ClienteRepository;
import java.util.List;

public class ClienteService {

    private ClienteRepository repository = new ClienteRepository();

    public String cadastrarCliente(Cliente cliente) {
        
        if (repository.buscarPorCnpjCpf(cliente.getCnpjCpf()) != null) {
            return "Erro: CNPJ/CPF já cadastrado para outro cliente.";
        }

        repository.salvar(cliente);
        return "Cliente cadastrado com sucesso!";
    }

    public List<Cliente> listarClientes() {
        return repository.listarTodos();
    }
}