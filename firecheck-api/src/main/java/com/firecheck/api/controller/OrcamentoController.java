package com.firecheck.api.controller; // <<< Package declaration

// <<< Imports necessários >>>
import com.firecheck.api.dto.ItemOrcamentoDTO;
import com.firecheck.api.dto.OrcamentoDTO;
import com.firecheck.api.model.OrcamentoRequest;
import com.firecheck.api.service.OrcamentoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController // <<< Anotações da classe
@RequestMapping("/api/orcamentos")
@CrossOrigin(origins = "*")
public class OrcamentoController { // <<< Definição da classe

    @Autowired // <<< Injeção do Service
    private OrcamentoService orcamentoService;

    @PostMapping // <<< Anotação do método
    public ResponseEntity<?> criarOrcamento(@RequestBody OrcamentoRequest request) {
        try {
            OrcamentoDTO orcamento = orcamentoService.criarOrcamento(request);
            return ResponseEntity.ok(orcamento);
        } catch (Exception e) {
            // Retorna a mensagem de erro específica do Service
            return ResponseEntity.badRequest().body("Erro ao criar orçamento: " + e.getMessage());
        }
    }

    @GetMapping // <<< Anotação do método
    public ResponseEntity<List<OrcamentoDTO>> listarOrcamentos() {
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    @GetMapping("/{id}/itens") // <<< Anotação do método
    public ResponseEntity<?> listarItensDoOrcamento(@PathVariable Long id) {
         try {
             // Chama buscarOrcamentoPorId primeiro para garantir que existe (retorna 404 se não)
             orcamentoService.buscarOrcamentoPorId(id);
             List<ItemOrcamentoDTO> itens = orcamentoService.listarItensDoOrcamento(id);
             return ResponseEntity.ok(itens);
         } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
         } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro ao buscar itens do orçamento: " + e.getMessage());
         }
    }

     @GetMapping("/{id}")
    public ResponseEntity<OrcamentoDTO> buscarOrcamentoPorId(@PathVariable Long id) {
        try {
            OrcamentoDTO dto = orcamentoService.buscarOrcamentoDTOPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusOrcamento(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
         try {
             String novoStatus = payload.get("status");
             if (novoStatus == null || novoStatus.trim().isEmpty()) {
                 return ResponseEntity.badRequest().body("Erro: Novo 'status' é obrigatório.");
             }
             OrcamentoDTO dto = orcamentoService.atualizarStatusOrcamento(id, novoStatus);
             return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
             return ResponseEntity.notFound().build();
         } catch (IllegalArgumentException | IllegalStateException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
              return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
         }
    }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deletarOrcamento(@PathVariable Long id) {
        try {
            orcamentoService.deletarOrcamento(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
} // <<< Fechamento da classe