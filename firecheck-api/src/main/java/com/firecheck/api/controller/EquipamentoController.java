package com.firecheck.api.controller;

import com.firecheck.api.dto.EquipamentoDTO;
import com.firecheck.api.model.Equipamento;
import com.firecheck.api.service.EquipamentoService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/equipamentos")
@CrossOrigin(origins = "*")
public class EquipamentoController {

    @Autowired
    private EquipamentoService equipamentoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, Object> payload) {
         try {
            Object idEdificacaoObj = payload.get("idEdificacao"); Long idEdificacao = null;
            if (idEdificacaoObj == null) return ResponseEntity.badRequest().body("Erro: 'idEdificacao' é obrigatório.");
            if (idEdificacaoObj instanceof String) idEdificacao = Long.parseLong((String) idEdificacaoObj);
            else if (idEdificacaoObj instanceof Number) idEdificacao = ((Number) idEdificacaoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idEdificacao'.");

            String tipo = (String) payload.get("tipoEquipamento"); String local = (String) payload.get("localizacao");
            String dataFabStr = (String) payload.get("dataFabricacao"); String dataValStr = (String) payload.get("dataValidade");
            if(tipo == null || local == null || dataFabStr == null || dataValStr == null) return ResponseEntity.badRequest().body("Erro: Campos obrigatórios faltando.");

            Equipamento equipamento = new Equipamento();
            equipamento.setTipoEquipamento(tipo); equipamento.setLocalizacao(local);
            equipamento.setDataFabricacao(LocalDate.parse(dataFabStr)); equipamento.setDataValidade(LocalDate.parse(dataValStr));

            EquipamentoDTO dto = equipamentoService.cadastrarEquipamento(equipamento, idEdificacao);
            return ResponseEntity.ok(dto);
        } catch (NumberFormatException | ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Dados inválidos na requisição.");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Erro: " + e.getMessage()); }
    }

    @GetMapping
    public ResponseEntity<List<EquipamentoDTO>> listarTodos() {
        return ResponseEntity.ok(equipamentoService.listarTodos());
    }

    @GetMapping("/edificacao/{idEdificacao}")
    public ResponseEntity<List<EquipamentoDTO>> listarPorEdificacao(@PathVariable Long idEdificacao) {
        return ResponseEntity.ok(equipamentoService.listarPorEdificacao(idEdificacao));
    }

     // --- ATUALIZADO: GET por ID ---
     @GetMapping("/{id}")
     public ResponseEntity<EquipamentoDTO> buscarPorId(@PathVariable Long id) {
          try {
              EquipamentoDTO dto = equipamentoService.buscarEquipamentoDTOPorId(id);
              return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
         }
     }
     // --- FIM GET por ID ---

    // --- NOVO: PUT (Atualizar) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEquipamento(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            // Extrai e converte ID da Edificação
            Object idEdificacaoObj = payload.get("idEdificacao"); Long idEdificacaoNova = null;
            if (idEdificacaoObj == null) return ResponseEntity.badRequest().body("Erro: 'idEdificacao' é obrigatório.");
            if (idEdificacaoObj instanceof String) idEdificacaoNova = Long.parseLong((String) idEdificacaoObj);
            else if (idEdificacaoObj instanceof Number) idEdificacaoNova = ((Number) idEdificacaoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idEdificacao'.");

            // Extrai outros dados
            String tipo = (String) payload.get("tipoEquipamento"); String local = (String) payload.get("localizacao");
            String dataFabStr = (String) payload.get("dataFabricacao"); String dataValStr = (String) payload.get("dataValidade");
             if(tipo == null || local == null || dataFabStr == null || dataValStr == null) return ResponseEntity.badRequest().body("Erro: Campos obrigatórios faltando.");

            Equipamento equipamentoAtualizado = new Equipamento();
            equipamentoAtualizado.setTipoEquipamento(tipo); equipamentoAtualizado.setLocalizacao(local);
            equipamentoAtualizado.setDataFabricacao(LocalDate.parse(dataFabStr)); equipamentoAtualizado.setDataValidade(LocalDate.parse(dataValStr));

            EquipamentoDTO dto = equipamentoService.atualizarEquipamento(id, equipamentoAtualizado, idEdificacaoNova);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 se Equipamento ou Edificação não existem
        } catch (NumberFormatException | ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Dados inválidos na requisição.");
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
    // --- FIM PUT ---

    // --- NOVO: DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarEquipamento(@PathVariable Long id) {
        try {
            equipamentoService.deletarEquipamento(id);
            return ResponseEntity.noContent().build(); // 204 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 (dependências)
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
    // --- FIM DELETE ---
}