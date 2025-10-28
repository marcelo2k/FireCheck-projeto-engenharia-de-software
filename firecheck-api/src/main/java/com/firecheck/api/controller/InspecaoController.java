package com.firecheck.api.controller;

import com.firecheck.api.dto.InspecaoDTO;
import com.firecheck.api.model.Inspecao;
import com.firecheck.api.service.InspecaoService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime; // Importar
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inspecoes")
@CrossOrigin(origins = "*")
public class InspecaoController {

    @Autowired
    private InspecaoService inspecaoService;

    @PostMapping
    public ResponseEntity<?> agendar(@RequestBody Map<String, Object> payload) {
        try {
            // Conversão segura de IDs
            Object idTecnicoObj = payload.get("idTecnico"); Long idTecnico = null;
            if (idTecnicoObj == null) return ResponseEntity.badRequest().body("Erro: 'idTecnico' obrigatório.");
            if (idTecnicoObj instanceof String) idTecnico = Long.parseLong((String) idTecnicoObj);
            else if (idTecnicoObj instanceof Number) idTecnico = ((Number) idTecnicoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idTecnico'.");

            Object idEdificacaoObj = payload.get("idEdificacao"); Long idEdificacao = null;
            if (idEdificacaoObj == null) return ResponseEntity.badRequest().body("Erro: 'idEdificacao' obrigatório.");
            if (idEdificacaoObj instanceof String) idEdificacao = Long.parseLong((String) idEdificacaoObj);
            else if (idEdificacaoObj instanceof Number) idEdificacao = ((Number) idEdificacaoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idEdificacao'.");

            String dataInspecaoStr = (String) payload.get("dataInspecao"); String status = (String) payload.get("status");
            if (dataInspecaoStr == null || status == null) return ResponseEntity.badRequest().body("Erro: Data e Status são obrigatórios.");

            Inspecao inspecao = new Inspecao();
            inspecao.setDataInspecao(LocalDateTime.parse(dataInspecaoStr)); inspecao.setStatus(status);

            InspecaoDTO dto = inspecaoService.agendarInspecao(inspecao, idTecnico, idEdificacao);
            return ResponseEntity.ok(dto);
        } catch (NumberFormatException | ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Dados inválidos.");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Erro: " + e.getMessage()); }
    }

    @GetMapping
    public ResponseEntity<List<InspecaoDTO>> listarTodas() {
        return ResponseEntity.ok(inspecaoService.listarTodas());
    }

    // --- ATUALIZADO: GET por ID ---
     @GetMapping("/{id}")
     public ResponseEntity<InspecaoDTO> buscarPorId(@PathVariable Long id) {
         try {
             InspecaoDTO dto = inspecaoService.buscarInspecaoDTOPorId(id);
             return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
         }
     }
     // --- FIM GET por ID ---

    // --- NOVO: PUT (Atualizar) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarInspecao(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
         try {
             // Conversão segura de IDs
            Object idTecnicoObj = payload.get("idTecnico"); Long idTecnicoNovo = null;
            if (idTecnicoObj == null) return ResponseEntity.badRequest().body("Erro: 'idTecnico' obrigatório.");
            if (idTecnicoObj instanceof String) idTecnicoNovo = Long.parseLong((String) idTecnicoObj);
            else if (idTecnicoObj instanceof Number) idTecnicoNovo = ((Number) idTecnicoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idTecnico'.");

            Object idEdificacaoObj = payload.get("idEdificacao"); Long idEdificacaoNova = null;
            if (idEdificacaoObj == null) return ResponseEntity.badRequest().body("Erro: 'idEdificacao' obrigatório.");
            if (idEdificacaoObj instanceof String) idEdificacaoNova = Long.parseLong((String) idEdificacaoObj);
            else if (idEdificacaoObj instanceof Number) idEdificacaoNova = ((Number) idEdificacaoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idEdificacao'.");

            String dataInspecaoStr = (String) payload.get("dataInspecao"); String status = (String) payload.get("status");
            if (dataInspecaoStr == null || status == null) return ResponseEntity.badRequest().body("Erro: Data e Status são obrigatórios.");

            Inspecao inspecaoAtualizada = new Inspecao();
            inspecaoAtualizada.setDataInspecao(LocalDateTime.parse(dataInspecaoStr));
            inspecaoAtualizada.setStatus(status);

            InspecaoDTO dto = inspecaoService.atualizarInspecao(id, inspecaoAtualizada, idTecnicoNovo, idEdificacaoNova);
            return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 se Inspecao, Tecnico ou Edificacao não existem
         } catch (NumberFormatException | ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Dados inválidos.");
         } catch (Exception e) {
              return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
         }
    }
    // --- FIM PUT ---

    // --- NOVO: DELETE ---
     @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarInspecao(@PathVariable Long id) {
        try {
            inspecaoService.deletarInspecao(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Tem itens
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
    // --- FIM DELETE ---
}