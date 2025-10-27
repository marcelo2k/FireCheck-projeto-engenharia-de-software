package service;

import model.Usuario;
import repository.UsuarioRepository;
import java.util.List;

public class UsuarioService {

    private UsuarioRepository repository = new UsuarioRepository();
    public String cadastrarUsuario(Usuario usuario) {
        
        if (repository.buscarPorLogin(usuario.getLogin()) != null) {
            return "Erro: Login já está em uso.";
        }
        
        if (repository.buscarPorCpf(usuario.getCpf()) != null) {
            return "Erro: CPF já cadastrado.";
        }

        repository.salvar(usuario);
        return "Usuário cadastrado com sucesso!";
    }
    public List<Usuario> listarUsuarios() {
        return repository.listarTodos();
    }
    
}