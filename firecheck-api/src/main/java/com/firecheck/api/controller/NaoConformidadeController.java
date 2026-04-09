package com.firecheck.api.controller; // <<< Package declaration

// <<< Imports necessários >>>
import com.firecheck.api.dto.NaoConformidadeDTO;
import com.firecheck.api.model.NaoConformidade;
import com.firecheck.api.service.NaoConformidadeService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController // <<< Anotações da classe
@RequestMapping("/api/nao-conformidades")
@CrossOrigin(origins = "*")
public class NaoConformidadeController { // <<< Definição da classe

    @Autowired // <<< Injeção do Service
    private NaoConformidadeService ncService;

    @PostMapping // <<< Anotação do método
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> payload) {
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

            // Criar objeto NaoConformidade
            String descricao = (String) payload.get("descricao");
            String fotoUrl = (String) payload.get("fotoUrl"); // Opcional

            if (descricao == null || descricao.trim().isEmpty()) {
                 return ResponseEntity.badRequest().body("Erro: 'descricao' é obrigatória.");
            }

            NaoConformidade nc = new NaoConformidade();
            nc.setDescricao(descricao);
            nc.setFotoUrl(fotoUrl);

            NaoConformidadeDTO dto = ncService.registrarNaoConformidade(nc, idInspecao, idEquipamento);
            return ResponseEntity.ok(dto);
        } catch (NumberFormatException nfe) {
             return ResponseEntity.badRequest().body("Erro: ID inválido (não é um número).");
        } catch (ClassCastException cce){
             return ResponseEntity.badRequest().body("Erro: Tipo inválido para descricao ou fotoUrl (devem ser texto).");
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("Erro ao registrar não conformidade: " + e.getMessage());
        }
    }

    @GetMapping("/inspecao/{idInspecao}") // <<< Anotação do método
    public ResponseEntity<List<NaoConformidadeDTO>> listarPorInspecao(@PathVariable Long idInspecao) {
        return ResponseEntity.ok(ncService.listarPorInspecao(idInspecao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NaoConformidadeDTO> buscarNCPorId(@PathVariable Long id) {
        try {
            NaoConformidadeDTO dto = ncService.buscarNCDTOPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarNaoConformidade(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
         try {
             String descricao = (String) payload.get("descricao");
             String fotoUrl = (String) payload.get("fotoUrl");
             if (descricao == null || descricao.trim().isEmpty()) {
                 return ResponseEntity.badRequest().body("Erro: 'descricao' é obrigatória.");
             }

             NaoConformidade ncAtualizada = new NaoConformidade();
             ncAtualizada.setDescricao(descricao);
             ncAtualizada.setFotoUrl(fotoUrl);

             NaoConformidadeDTO dto = ncService.atualizarNaoConformidade(id, ncAtualizada);
             return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
             return ResponseEntity.notFound().build();
         } catch (ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Tipo inválido para descricao ou fotoUrl.");
         } catch (Exception e) {
              return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
         }
    }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deletarNaoConformidade(@PathVariable Long id) {
        try {
            ncService.deletarNaoConformidade(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
} // <<< Fechamento da classe