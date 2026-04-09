package com.firecheck.api.controller; // <<< Package declaration

// <<< Imports necessários >>>
import com.firecheck.api.dto.ItemInspecionadoDTO;
import com.firecheck.api.model.ItemInspecionado;
import com.firecheck.api.service.ItemInspecionadoService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController // <<< Anotações da classe
@RequestMapping("/api/itens-inspecionados")
@CrossOrigin(origins = "*")
public class ItemInspecionadoController { // <<< Definição da classe

    @Autowired // <<< Injeção do Service
    private ItemInspecionadoService itemService;

    @PostMapping // <<< Anotação do método
    public ResponseEntity<?> salvarItem(@RequestBody Map<String, Object> payload) {
         try {
            // Conversão segura de ID INSPECAO
            Object idInspecaoObj = payload.get("idInspecao");
            Long idInspecao = null;
            if (idInspecaoObj == null) return ResponseEntity.badRequest().body("Erro: 'idInspecao' é obrigatório.");
            if (idInspecaoObj instanceof String) idInspecao = Long.parseLong((String) idInspecaoObj);
            else if (idInspecaoObj instanceof Number) idInspecao = ((Number) idInspecaoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idInspecao'.");

            // Conversão segura de ID EQUIPAMENTO
            Object idEquipamentoObj = payload.get("idEquipamento");
            Long idEquipamento = null;
            if (idEquipamentoObj == null) return ResponseEntity.badRequest().body("Erro: 'idEquipamento' é obrigatório.");
            if (idEquipamentoObj instanceof String) idEquipamento = Long.parseLong((String) idEquipamentoObj);
            else if (idEquipamentoObj instanceof Number) idEquipamento = ((Number) idEquipamentoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idEquipamento'.");

            // Criar objeto ItemInspecionado
            String statusGeral = (String) payload.get("statusGeral");
            String observacoes = (String) payload.get("observacoes");
             if (statusGeral == null) {
                 return ResponseEntity.badRequest().body("Erro: 'statusGeral' é obrigatório.");
             }

            ItemInspecionado item = new ItemInspecionado();
            item.setStatusGeral(statusGeral);
            item.setObservacoes(observacoes);

            ItemInspecionadoDTO dto = itemService.salvarItem(item, idInspecao, idEquipamento);
            return ResponseEntity.ok(dto);
        } catch (NumberFormatException nfe) {
             return ResponseEntity.badRequest().body("Erro: ID inválido (não é um número).");
        } catch (ClassCastException cce){
             return ResponseEntity.badRequest().body("Erro: Tipo inválido para statusGeral ou observacoes (devem ser texto).");
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("Erro ao salvar item: " + e.getMessage());
        }
    }

    @GetMapping("/inspecao/{idInspecao}") // <<< Anotação do método
    public ResponseEntity<List<ItemInspecionadoDTO>> listarPorInspecao(@PathVariable Long idInspecao) {
        return ResponseEntity.ok(itemService.listarPorInspecao(idInspecao));
    }

    @GetMapping("/inspecao/{idInspecao}/equipamento/{idEquipamento}")
    public ResponseEntity<ItemInspecionadoDTO> buscarItemPorPk(
            @PathVariable Long idInspecao,
            @PathVariable Long idEquipamento) {
        try {
            ItemInspecionadoDTO dto = itemService.buscarItemDTOPorPk(idInspecao, idEquipamento);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/inspecao/{idInspecao}/equipamento/{idEquipamento}")
    public ResponseEntity<?> atualizarItem(
            @PathVariable Long idInspecao,
            @PathVariable Long idEquipamento,
            @RequestBody Map<String, Object> payload) {
         try {
             String statusGeral = (String) payload.get("statusGeral");
             String observacoes = (String) payload.get("observacoes");
             if (statusGeral == null) return ResponseEntity.badRequest().body("Erro: 'statusGeral' é obrigatório.");

             ItemInspecionado itemAtualizado = new ItemInspecionado();
             itemAtualizado.setStatusGeral(statusGeral);
             itemAtualizado.setObservacoes(observacoes);

             ItemInspecionadoDTO dto = itemService.atualizarItem(idInspecao, idEquipamento, itemAtualizado);
             return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
             return ResponseEntity.notFound().build();
         } catch (ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Tipo inválido para statusGeral ou observacoes.");
         } catch (Exception e) {
              return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
         }
    }

     @DeleteMapping("/inspecao/{idInspecao}/equipamento/{idEquipamento}")
     public ResponseEntity<?> deletarItem(
             @PathVariable Long idInspecao,
             @PathVariable Long idEquipamento) {
        try {
            itemService.deletarItem(idInspecao, idEquipamento);
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