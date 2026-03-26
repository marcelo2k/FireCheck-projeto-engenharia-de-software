package com.firecheck.api.controller;

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

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {

    @Autowired private ServicoService servicoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(processarServico(payload, null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarServico(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
         try {
            return ResponseEntity.ok(processarServico(payload, id));
         } catch (EntityNotFoundException e) { return ResponseEntity.notFound().build();
         } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    // Método auxiliar para evitar repetição de código
    private ServicoDTO processarServico(Map<String, Object> payload, Long idAtualizacao) {
        String nome = (String) payload.get("nome");
        String descricao = (String) payload.get("descricao");
        Object valorObj = payload.get("valorUnitario");
        Object tempoObj = payload.get("tempoExecucaoHoras");
        Object estoqueObj = payload.get("estoque");
        Object minObj = payload.get("estoqueMinimo"); // NOVO

        if (nome == null || valorObj == null) throw new IllegalArgumentException("Nome e Valor são obrigatórios.");
        
        BigDecimal valor = new BigDecimal(valorObj.toString());
        Double tempo = tempoObj != null && !tempoObj.toString().isEmpty() ? Double.parseDouble(tempoObj.toString()) : null;
        Integer estoque = estoqueObj != null && !estoqueObj.toString().isEmpty() ? Integer.parseInt(estoqueObj.toString()) : null;
        Integer min = minObj != null && !minObj.toString().isEmpty() ? Integer.parseInt(minObj.toString()) : null;

        Servico s = new Servico();
        s.setNome(nome); s.setDescricao(descricao); s.setValorUnitario(valor);
        s.setTempoExecucaoHoras(tempo); s.setEstoque(estoque); s.setEstoqueMinimo(min);

        if (idAtualizacao != null) return servicoService.atualizarServico(idAtualizacao, s);
        return servicoService.cadastrarServico(s);
    }

    @GetMapping
    public ResponseEntity<List<ServicoDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    // --- NOVO ENDPOINT: ALERTAS ---
    @GetMapping("/alertas")
    public ResponseEntity<List<ServicoDTO>> listarAlertas() {
        return ResponseEntity.ok(servicoService.listarBaixoEstoque());
    }
    // ------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
         try { return ResponseEntity.ok(servicoService.buscarDTOPorId(id)); }
         catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deletarServico(@PathVariable Long id) {
        try { servicoService.deletarServico(id); return ResponseEntity.noContent().build(); }
        catch (EntityNotFoundException e) { return ResponseEntity.notFound().build(); }
        catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}