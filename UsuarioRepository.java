package repository;
import java.util.ArrayList;
import java.util.List;

import model.Usuario; 

public class UsuarioRepository {
    private static List<Usuario> usuarios = new ArrayList<>();

    public void salvar(Usuario usuario) {
        usuarios.add(usuario);
    }

    public Usuario buscarPorLogin(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login)) {
                return u; 
            }
        }
        return null; 
    }
    
    public Usuario buscarPorCpf(String cpf) {
        for (Usuario u : usuarios) {
            if (u.getCpf().equals(cpf)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        return usuarios;
    }
    
}