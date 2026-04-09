package com.firecheck.api.controller;

import com.firecheck.api.model.Cliente;
import com.firecheck.api.service.ClienteService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<String> cadastrarCliente(@RequestBody Cliente cliente) {
        try {
            String resultado = clienteService.cadastrarCliente(cliente);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) { // Captura erro de CNPJ duplicado do save original
             return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado ao cadastrar cliente.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    // --- NOVO ENDPOINT: BUSCAR POR ID (útil para edição) ---
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
         try {
             Cliente cliente = clienteService.buscarClientePorId(id);
             return ResponseEntity.ok(cliente);
         } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
         }
    }
    // --- FIM NOVO ENDPOINT ---

    // --- NOVO ENDPOINT: ATUALIZAR ---
    @PutMapping("/{id}") // Responde a PUT /api/clientes/1
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        try {
            Cliente clienteSalvo = clienteService.atualizarCliente(id, clienteAtualizado);
            return ResponseEntity.ok(clienteSalvo); // Retorna o cliente atualizado
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o ID não existe
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 se CNPJ duplicado
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado ao atualizar cliente.");
        }
    }
    // --- FIM NOVO ENDPOINT ---

    // --- NOVO ENDPOINT: DELETAR ---
    @DeleteMapping("/{id}") // Responde a DELETE /api/clientes/1
    public ResponseEntity<?> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content (sucesso sem corpo)
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o ID não existe
        } catch (IllegalStateException e) {
             return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 se tiver edificações
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado ao deletar cliente.");
        }
    }
    // --- FIM NOVO ENDPOINT ---
}