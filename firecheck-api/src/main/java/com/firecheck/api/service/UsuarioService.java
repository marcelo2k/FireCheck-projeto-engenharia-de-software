package com.firecheck.api.service;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.repository.UsuarioRepository;
// Importar InspecaoRepository e OrcamentoRepository para checar dependências (opcional)
// import com.firecheck.api.repository.InspecaoRepository;
// import com.firecheck.api.repository.OrcamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Injetar outros repositórios se for fazer a checagem de dependência antes de excluir
    // @Autowired private InspecaoRepository inspecaoRepository;
    // @Autowired private OrcamentoRepository orcamentoRepository;

    public String cadastrarUsuario(Usuario usuario) {
        Optional<Usuario> existenteLogin = usuarioRepository.findByLogin(usuario.getLogin());
        if (existenteLogin.isPresent()) {
            return "Erro: Login já está em uso.";
        }
        Optional<Usuario> existenteCpf = usuarioRepository.findByCpf(usuario.getCpf());
        if (existenteCpf.isPresent()) {
            return "Erro: CPF já cadastrado.";
        }
        
        usuarioRepository.save(usuario);
        return "Usuário cadastrado com sucesso!";
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorId(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        return usuarioOpt.orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado."));
    }

    // --- NOVO MÉTODO: ATUALIZAR ---
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        // 1. Busca o usuário existente (lança 404 se não achar)
        Usuario usuarioExistente = buscarUsuarioPorId(id);

        // 2. Valida Login único (se diferente do atual)
        Optional<Usuario> conflitoLogin = usuarioRepository.findByLogin(usuarioAtualizado.getLogin());
        if (conflitoLogin.isPresent() && !conflitoLogin.get().getId().equals(id)) {
            throw new IllegalArgumentException("Erro: Login já pertence a outro usuário.");
        }

        // 3. Valida CPF único (se diferente do atual)
        Optional<Usuario> conflitoCpf = usuarioRepository.findByCpf(usuarioAtualizado.getCpf());
        if (conflitoCpf.isPresent() && !conflitoCpf.get().getId().equals(id)) {
            throw new IllegalArgumentException("Erro: CPF já pertence a outro usuário.");
        }

        // 4. Atualiza os dados (exceto ID e senha - senha geralmente é atualizada em endpoint separado)
        usuarioExistente.setNomeCompleto(usuarioAtualizado.getNomeCompleto());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
        usuarioExistente.setLogin(usuarioAtualizado.getLogin());
        // Não atualizamos a senha aqui por segurança. Se precisar, criar método específico.
        // usuarioExistente.setSenha(usuarioAtualizado.getSenha());

        // 5. Salva (Update)
        return usuarioRepository.save(usuarioExistente);
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: DELETAR ---
    public void deletarUsuario(Long id) {
        // 1. Verifica se existe
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário com ID " + id + " não encontrado.");
        }

        // 2. (Opcional) Verifica dependências (Ex: Inspeções ou Orçamentos criados por ele)
        //    Isso requer que os respectivos repositórios (InspecaoRepository, OrcamentoRepository)
        //    estejam migrados para JPA e tenham métodos como findByUsuarioId() ou findByTecnicoId()
        // Exemplo (se InspecaoRepository for JPA):
        // if (!inspecaoRepository.findByTecnicoId(id).isEmpty()) {
        //     throw new IllegalStateException("Erro: Não é possível excluir o usuário pois ele possui inspeções associadas.");
        // }
        // Exemplo (se OrcamentoRepository for JPA):
        // if (!orcamentoRepository.findByUsuarioId(id).isEmpty()) {
        //      throw new IllegalStateException("Erro: Não é possível excluir o usuário pois ele possui orçamentos associados.");
        // }


        // 3. Deleta
        usuarioRepository.deleteById(id);
    }
    // --- FIM NOVO MÉTODO ---
}