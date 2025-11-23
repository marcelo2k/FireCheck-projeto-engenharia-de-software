package com.firecheck.api.controller;

import com.firecheck.api.dto.EdificacaoDTO;
import com.firecheck.api.model.Edificacao; // Usado para criar o objeto
import com.firecheck.api.service.EdificacaoService;
import jakarta.persistence.EntityNotFoundException; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/edificacoes")
@CrossOrigin(origins = "*")
public class EdificacaoController {

    @Autowired
    private EdificacaoService edificacaoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, Object> payload) {
        try {
            // Extração e conversão segura de ID (como feito antes)
            Object idClienteObj = payload.get("idCliente"); Long idCliente = null;
            if (idClienteObj == null) return ResponseEntity.badRequest().body("Erro: 'idCliente' é obrigatório.");
            if (idClienteObj instanceof String) idCliente = Long.parseLong((String) idClienteObj);
            else if (idClienteObj instanceof Number) idCliente = ((Number) idClienteObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idCliente'.");

            String nome = (String) payload.get("nome");
            String endereco = (String) payload.get("endereco");
            String cep = (String) payload.get("cep");
             if (nome == null || endereco == null || cep == null) {
                  return ResponseEntity.badRequest().body("Erro: Nome, Endereço e CEP são obrigatórios.");
             }

            Edificacao edificacao = new Edificacao();
            edificacao.setNome(nome);
            edificacao.setEndereco(endereco);
            edificacao.setCep(cep);

            // Chama o service que retorna DTO
            EdificacaoDTO dto = edificacaoService.cadastrarEdificacao(edificacao, idCliente);
            // Retorna a mensagem original ou o DTO (escolha um padrão)
            // return ResponseEntity.ok("Edificação cadastrada com sucesso para o cliente ID: " + idCliente); // Ou
             return ResponseEntity.ok(dto); // Retorna o DTO criado

        } catch (NumberFormatException nfe) {
             return ResponseEntity.badRequest().body("Erro: ID inválido.");
        } catch (ClassCastException cce){
             return ResponseEntity.badRequest().body("Erro: Tipo inválido para campos de texto.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar edificação: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<EdificacaoDTO>> listarTodas() {
        return ResponseEntity.ok(edificacaoService.listarTodas());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<EdificacaoDTO>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(edificacaoService.listarPorIdCliente(idCliente));
    }

    // --- NOVO ENDPOINT: BUSCAR POR ID ---
    @GetMapping("/{id}")
    public ResponseEntity<EdificacaoDTO> buscarEdificacaoPorId(@PathVariable Long id) {
         try {
             EdificacaoDTO dto = edificacaoService.buscarEdificacaoDTOPorId(id);
             return ResponseEntity.ok(dto);
         } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
         }
    }
    // --- FIM NOVO ENDPOINT ---

    // --- NOVO ENDPOINT: ATUALIZAR ---
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEdificacao(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
         try {
            // Extração e conversão segura do NOVO idCliente
            Object idClienteNovoObj = payload.get("idCliente"); Long idClienteNovo = null;
            if (idClienteNovoObj == null) return ResponseEntity.badRequest().body("Erro: 'idCliente' é obrigatório para atualização.");
            if (idClienteNovoObj instanceof String) idClienteNovo = Long.parseLong((String) idClienteNovoObj);
            else if (idClienteNovoObj instanceof Number) idClienteNovo = ((Number) idClienteNovoObj).longValue();
            else return ResponseEntity.badRequest().body("Erro: Tipo inesperado para 'idCliente'.");

            String nome = (String) payload.get("nome");
            String endereco = (String) payload.get("endereco");
            String cep = (String) payload.get("cep");
            if (nome == null || endereco == null || cep == null) {
                  return ResponseEntity.badRequest().body("Erro: Nome, Endereço e CEP são obrigatórios.");
             }

            // Cria um objeto temporário com os dados atualizados
            Edificacao edificacaoAtualizada = new Edificacao();
            edificacaoAtualizada.setNome(nome);
            edificacaoAtualizada.setEndereco(endereco);
            edificacaoAtualizada.setCep(cep);
            // O cliente será buscado e setado no Service

            EdificacaoDTO dtoSalvo = edificacaoService.atualizarEdificacao(id, edificacaoAtualizada, idClienteNovo);
            return ResponseEntity.ok(dtoSalvo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Erro 404 se Edificação ou Cliente não existem
        } catch (NumberFormatException | ClassCastException e) {
             return ResponseEntity.badRequest().body("Erro: Dados inválidos na requisição.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado ao atualizar edificação: " + e.getMessage());
        }
    }
    // --- FIM NOVO ENDPOINT ---

    // --- NOVO ENDPOINT: DELETAR ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarEdificacao(@PathVariable Long id) {
        try {
            edificacaoService.deletarEdificacao(id);
            return ResponseEntity.noContent().build(); // 204 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (IllegalStateException e) {
             return ResponseEntity.badRequest().body(e.getMessage()); // 400 (ex: tem equipamentos)
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado ao deletar edificação."); // 500
        }
    }
    // --- FIM NOVO ENDPOINT ---
}