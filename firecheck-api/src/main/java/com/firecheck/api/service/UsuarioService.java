package com.firecheck.api.service;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Memória temporária para guardar os códigos gerados (Login -> Código)
    private final Map<String, String> codigosRecuperacao = new ConcurrentHashMap<>();

    public String cadastrarUsuario(Usuario usuario) {
        Optional<Usuario> existenteLogin = repository.findByLogin(usuario.getLogin());
        if (existenteLogin.isPresent()) return "Erro: Login já está em uso.";

        Optional<Usuario> existenteCpf = repository.findByCpf(usuario.getCpf());
        if (existenteCpf.isPresent()) return "Erro: CPF já cadastrado.";

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        if (usuario.getPerfil() == null || usuario.getPerfil().isEmpty()) {
            usuario.setPerfil("TECNICO");
        }

        repository.save(usuario);
        return "Usuário cadastrado com sucesso!";
    }

    public List<Usuario> listarUsuarios() {
        return repository.findAll();
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado."));
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarUsuarioPorId(id);

        Optional<Usuario> conflitoLogin = repository.findByLogin(usuarioAtualizado.getLogin());
        if (conflitoLogin.isPresent() && !conflitoLogin.get().getId().equals(id)) {
            throw new IllegalArgumentException("Erro: Login já pertence a outro usuário.");
        }
        Optional<Usuario> conflitoCpf = repository.findByCpf(usuarioAtualizado.getCpf());
        if (conflitoCpf.isPresent() && !conflitoCpf.get().getId().equals(id)) {
            throw new IllegalArgumentException("Erro: CPF já pertence a outro usuário.");
        }

        usuarioExistente.setNomeCompleto(usuarioAtualizado.getNomeCompleto());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
        usuarioExistente.setLogin(usuarioAtualizado.getLogin());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil()); 

        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        return repository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Usuário não encontrado.");
        repository.deleteById(id);
    }

    public Usuario autenticar(String login, String senhaPura) {
        Usuario usuario = repository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos."));

        if (passwordEncoder.matches(senhaPura, usuario.getSenha())) {
            return usuario;
        } else {
            throw new IllegalArgumentException("Login ou senha inválidos.");
        }
    }

    // --- PASSO 1: GERAR CÓDIGO E ENVIAR E-MAIL ---
    public void solicitarCodigoReset(String login) {
        Usuario usuario = repository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if ("ADMIN".equalsIgnoreCase(usuario.getPerfil())) {
            throw new IllegalArgumentException("Ação não permitida para Administradores. Contate o suporte.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Este usuário não possui um e-mail cadastrado no sistema.");
        }

        String codigo = emailService.gerarCodigoVerificacao();
        codigosRecuperacao.put(login, codigo); // Salva na memória RAM
        
        emailService.enviarEmailRecuperacao(usuario.getEmail(), codigo);
    }

    // --- PASSO 2: VALIDAR CÓDIGO E TROCAR A SENHA ---
    public void redefinirSenhaComCodigo(String login, String codigo, String novaSenha) {
        Usuario usuario = repository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if ("ADMIN".equalsIgnoreCase(usuario.getPerfil())) {
            throw new IllegalArgumentException("Ação não permitida para Administradores.");
        }

        // Verifica se o código digitado é igual ao que está na memória
        String codigoSalvo = codigosRecuperacao.get(login);
        if (codigoSalvo == null || !codigoSalvo.equals(codigo)) {
            throw new IllegalArgumentException("Código de verificação inválido ou expirado.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);
        
        // Limpa o código da memória para não ser usado de novo
        codigosRecuperacao.remove(login); 
    }
}