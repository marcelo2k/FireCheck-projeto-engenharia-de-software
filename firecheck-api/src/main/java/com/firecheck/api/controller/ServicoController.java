package com.firecheck.api.controller; // <<< Package declaration

// <<< Imports necessários >>>
import com.firecheck.api.dto.ServicoDTO;
import com.firecheck.api.model.Servico;
import com.firecheck.api.service.ServicoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RestController // <<< Anotações da classe
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
public class ServicoController { // <<< Definição da classe

    @Autowired // <<< Injeção do Service
    private ServicoService servicoService;

    @PostMapping // <<< Anotação do método
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, Object> payload) {
        try {
            String nome = (String) payload.get("nome");
            String descricao = (String) payload.get("descricao");
            Object valorUnitarioObj = payload.get("valorUnitario");
            Object tempoExecucaoObj = payload.get("tempoExecucaoHoras");
            Object estoqueObj = payload.get("estoque");

            if (nome == null || nome.trim().isEmpty() || valorUnitarioObj == null) {
                 return ResponseEntity.badRequest().body("Erro: Nome e Valor Unitário são obrigatórios.");
            }

            BigDecimal valorUnitario; Double tempoExecucaoHoras = null; Integer estoque = null;

            try { valorUnitario = new BigDecimal(valorUnitarioObj.toString()); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body("Erro: Valor Unitário inválido.");}
            if (tempoExecucaoObj != null && !tempoExecucaoObj.toString().isEmpty()) { try { tempoExecucaoHoras = Double.parseDouble(tempoExecucaoObj.toString()); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body("Erro: Tempo inválido."); } }
            if (estoqueObj != null && !estoqueObj.toString().isEmpty()) { try { if (estoqueObj instanceof String) estoque = Integer.parseInt((String) estoqueObj); else if (estoqueObj instanceof Number) estoque = ((Number) estoqueObj).intValue(); else return ResponseEntity.badRequest().body("Erro: Tipo Estoque inválido."); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body("Erro: Estoque inválido."); } }

            Servico servico = new Servico();
            servico.setNome(nome); servico.setDescricao(descricao);
            servico.setValorUnitario(valorUnitario); servico.setTempoExecucaoHoras(tempoExecucaoHoras);
            servico.setEstoque(estoque);

            ServicoDTO dto = servicoService.cadastrarServico(servico);
            return ResponseEntity.ok(dto);

        } catch (ClassCastException cce){
             return ResponseEntity.badRequest().body("Erro: Tipo inválido para nome ou descrição (devem ser texto).");
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("Erro ao cadastrar serviço: " + e.getMessage());
        }
    }

    @GetMapping // <<< Anotação do método
    public ResponseEntity<List<ServicoDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/{id}") // <<< Anotação do método
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
         try {
             ServicoDTO dto = servicoService.buscarDTOPorId(id);
             return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) { // Captura erro do service
             return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarServico(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
         try {
            // Extrai dados e faz conversões seguras (similar ao POST)
            String nome = (String) payload.get("nome"); String descricao = (String) payload.get("descricao");
            Object valorUnitarioObj = payload.get("valorUnitario"); Object tempoExecucaoObj = payload.get("tempoExecucaoHoras"); Object estoqueObj = payload.get("estoque");
            if (nome == null || nome.trim().isEmpty() || valorUnitarioObj == null) return ResponseEntity.badRequest().body("Erro: Nome e Valor Unitário obrigatórios.");
            BigDecimal valorUnitario; Double tempoExecucaoHoras = null; Integer estoque = null;
            try { valorUnitario = new BigDecimal(valorUnitarioObj.toString()); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body("Erro: Valor Unitário inválido.");}
            if (tempoExecucaoObj != null && !tempoExecucaoObj.toString().isEmpty()) { try { tempoExecucaoHoras = Double.parseDouble(tempoExecucaoObj.toString()); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body("Erro: Tempo inválido."); } }
            if (estoqueObj != null && !estoqueObj.toString().isEmpty()) { try { if (estoqueObj instanceof String) estoque = Integer.parseInt((String) estoqueObj); else if (estoqueObj instanceof Number) estoque = ((Number) estoqueObj).intValue(); else return ResponseEntity.badRequest().body("Erro: Tipo Estoque inválido."); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body("Erro: Estoque inválido."); } }

            Servico servicoAtualizado = new Servico();
            servicoAtualizado.setNome(nome); servicoAtualizado.setDescricao(descricao);
            servicoAtualizado.setValorUnitario(valorUnitario); servicoAtualizado.setTempoExecucaoHoras(tempoExecucaoHoras);
            servicoAtualizado.setEstoque(estoque);

            ServicoDTO dto = servicoService.atualizarServico(id, servicoAtualizado);
            return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) { return ResponseEntity.notFound().build();
         } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage());
         } catch (ClassCastException e) { return ResponseEntity.badRequest().body("Erro: Tipo inválido para nome ou descrição.");
         } catch (Exception e) { return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage()); }
    }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deletarServico(@PathVariable Long id) {
        try {
            servicoService.deletarServico(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) { return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) { return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage()); }
    }
} // <<< Fechamento da classe