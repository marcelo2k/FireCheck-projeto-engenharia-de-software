package repository;

import java.util.ArrayList;
import java.util.List;
import model.Cliente; 
public class ClienteRepository {

    private static List<Cliente> clientes = new ArrayList<>();

    public void salvar(Cliente cliente) {
        clientes.add(cliente);
    }

    public Cliente buscarPorCnpjCpf(String cnpjCpf) {
        for (Cliente c : clientes) {
            if (c.getCnpjCpf().equals(cnpjCpf)) {
                return c;
            }
        }
        return null;
    }

    public List<Cliente> listarTodos() {
        return clientes;
    }
}