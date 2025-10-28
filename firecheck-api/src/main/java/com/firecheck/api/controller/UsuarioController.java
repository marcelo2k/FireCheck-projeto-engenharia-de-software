package com.firecheck.api.controller;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario usuario) {
         // O Service já retorna a String de sucesso/erro
         try {
             String resultado = usuarioService.cadastrarUsuario(usuario);
             // Verifica se o service retornou erro (começando com "Erro:")
             if (resultado.startsWith("Erro:")) {
                  return ResponseEntity.badRequest().body(resultado);
             }
             return ResponseEntity.ok(resultado); // Retorna a msg de sucesso
         } catch (Exception e) { // Captura outras exceções inesperadas
              return ResponseEntity.internalServerError().body("Erro inesperado ao cadastrar usuário.");
         }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
        // NOTA: Retornar a entidade Usuario diretamente pode expor a senha (hashed).
        // Em um app real, criaríamos um UsuarioDTO sem a senha.
    }

    // --- NOVO ENDPOINT: BUSCAR POR ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
         try {
             Usuario usuario = usuarioService.buscarUsuarioPorId(id);
             return ResponseEntity.ok(usuario); // Cuidado com a exposição da senha
         } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
         }
    }
    // --- FIM NOVO ENDPOINT ---

    // --- NOVO ENDPOINT: ATUALIZAR ---
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        try {
            // Remove a senha do objeto recebido para não tentar atualizá-la
            usuarioAtualizado.setSenha(null);
            Usuario usuarioSalvo = usuarioService.atualizarUsuario(id, usuarioAtualizado);
            return ResponseEntity.ok(usuarioSalvo); // Cuidado com a exposição da senha
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado ao atualizar usuário.");
        }
    }
    // --- FIM NOVO ENDPOINT ---

    // --- NOVO ENDPOINT: DELETAR ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
             return ResponseEntity.badRequest().body(e.getMessage()); // Erro se tiver dependências
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado ao deletar usuário.");
        }
    }
    // --- FIM NOVO ENDPOINT ---
}