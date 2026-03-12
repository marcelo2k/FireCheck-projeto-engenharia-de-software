package com.firecheck.api.controller; // <<< Package declaration

// <<< Imports necessários >>>
import com.firecheck.api.dto.OrdemServicoDTO;
import com.firecheck.api.service.OrdemServicoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; // Mover import para cá
import java.util.List;
import java.util.Map;

@RestController // <<< Anotações da classe
@RequestMapping("/api/ordens-servico")
@CrossOrigin(origins = "*")
public class OrdemServicoController { // <<< Definição da classe

    @Autowired // <<< Injeção do Service
    private OrdemServicoService osService;

    @PostMapping("/aprovar-orcamento/{idOrcamento}") // <<< Anotação do método
    public ResponseEntity<?> aprovarOrcamentoEGerarOS(
            @PathVariable Long idOrcamento,
            @RequestBody Map<String, String> payload) {
        try {
            String dataString = payload.get("dataExecucaoPrevista");
            if (dataString == null) {
                 return ResponseEntity.badRequest().body("Erro: 'dataExecucaoPrevista' é obrigatória.");
            }
            LocalDate dataExecucao = LocalDate.parse(dataString);

            OrdemServicoDTO os = osService.aprovarOrcamentoEGerarOS(idOrcamento, dataExecucao);
            return ResponseEntity.ok(os);
        } catch (Exception e) { // Captura EntityNotFound, IllegalArgument, ParseException etc.
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping // <<< Anotação do método
    public ResponseEntity<List<OrdemServicoDTO>> listarOrdens() {
        return ResponseEntity.ok(osService.listarTodas());
    }

     @GetMapping("/{id}") // <<< Anotação do método
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
             OrdemServicoDTO dto = osService.buscarDTOPorId(id);
             return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) { // Captura erro do service
             return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusOS(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
         try {
             String novoStatus = payload.get("status");
             if (novoStatus == null || novoStatus.trim().isEmpty()) {
                 return ResponseEntity.badRequest().body("Erro: Novo 'status' é obrigatório.");
             }
             OrdemServicoDTO dto = osService.atualizarStatusOS(id, novoStatus);
             return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
             return ResponseEntity.notFound().build();
         } catch (IllegalArgumentException | IllegalStateException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
              return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
         }
    }

     @PostMapping("/{id}/cancelar")
     public ResponseEntity<?> cancelarOS(@PathVariable Long id) {
        try {
            OrdemServicoDTO dto = osService.cancelarOS(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
} // <<< Fechamento da classe