package com.firecheck.api.service;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public String cadastrarUsuario(Usuario usuario) {
        Optional<Usuario> existenteLogin = repository.findByLogin(usuario.getLogin());
        if (existenteLogin.isPresent()) return "Erro: Login já está em uso.";

        Optional<Usuario> existenteCpf = repository.findByCpf(usuario.getCpf());
        if (existenteCpf.isPresent()) return "Erro: CPF já cadastrado.";

        repository.save(usuario);
        return "Usuário cadastrado com sucesso!";
    }

    public List<Usuario> listarUsuarios() {
        return repository.findAll();
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado."));
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarUsuarioPorId(id);
        Optional<Usuario> conflitoLogin = repository.findByLogin(usuarioAtualizado.getLogin());
        if (conflitoLogin.isPresent() && !conflitoLogin.get().getId().equals(id)) throw new IllegalArgumentException("Erro: Login já pertence a outro usuário.");

        Optional<Usuario> conflitoCpf = repository.findByCpf(usuarioAtualizado.getCpf());
        if (conflitoCpf.isPresent() && !conflitoCpf.get().getId().equals(id)) throw new IllegalArgumentException("Erro: CPF já pertence a outro usuário.");

        usuarioExistente.setNomeCompleto(usuarioAtualizado.getNomeCompleto());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
        usuarioExistente.setLogin(usuarioAtualizado.getLogin());
        // Senha não é atualizada aqui por segurança

        return repository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Usuário não encontrado.");
        // Verificações de dependência seriam aqui
        repository.deleteById(id);
    }

    // --- NOVO MÉTODO: AUTENTICAR ---
    public Usuario autenticar(String login, String senha) {
        return repository.findByLoginAndSenha(login, senha)
                .orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos."));
    }
}