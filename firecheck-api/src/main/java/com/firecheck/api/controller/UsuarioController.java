package com.firecheck.api.controller;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- NOVO ENDPOINT: LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        try {
            String login = credenciais.get("login");
            String senha = credenciais.get("senha");
            Usuario usuario = usuarioService.autenticar(login, senha);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 401 Unauthorized
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario usuario) {
         try {
             String resultado = usuarioService.cadastrarUsuario(usuario);
             if (resultado.startsWith("Erro:")) return ResponseEntity.badRequest().body(resultado);
             return ResponseEntity.ok(resultado);
         } catch (Exception e) { return ResponseEntity.internalServerError().body("Erro inesperado."); }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
         try { return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id)); } 
         catch (EntityNotFoundException e) { return ResponseEntity.notFound().build(); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        try {
            usuarioAtualizado.setSenha(null);
            Usuario usuarioSalvo = usuarioService.atualizarUsuario(id, usuarioAtualizado);
            return ResponseEntity.ok(usuarioSalvo);
        } catch (EntityNotFoundException e) { return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) { return ResponseEntity.internalServerError().body("Erro inesperado."); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) { return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) { return ResponseEntity.internalServerError().body("Erro inesperado."); }
    }
}