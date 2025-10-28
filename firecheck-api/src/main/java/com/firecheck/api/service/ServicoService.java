package com.firecheck.api.service;

import com.firecheck.api.dto.ServicoDTO;
import com.firecheck.api.model.Servico;
import com.firecheck.api.repository.ItemOrcamentoRepository; // Injeção correta
import com.firecheck.api.repository.ServicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoService {

    @Autowired private ServicoRepository servicoRepository;
    @Autowired private ItemOrcamentoRepository itemOrcamentoRepository; // Injeção correta

    public ServicoDTO cadastrarServico(Servico servico) {
        Optional<Servico> existente = servicoRepository.findByNomeIgnoreCase(servico.getNome());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Erro: Já existe um serviço cadastrado com este nome.");
        }
        if (servico.getValorUnitario() == null || servico.getValorUnitario().doubleValue() < 0) {
            throw new IllegalArgumentException("Erro: O valor unitário é obrigatório e não pode ser negativo.");
        }
        if (servico.getEstoque() != null && servico.getEstoque() < 0) {
             throw new IllegalArgumentException("Erro: O estoque não pode ser negativo.");
         }
        if (servico.getTempoExecucaoHoras() != null && servico.getTempoExecucaoHoras() < 0) {
              throw new IllegalArgumentException("Erro: O tempo de execução não pode ser negativo.");
         }
        Servico salvo = servicoRepository.save(servico);
        return new ServicoDTO(salvo);
    }

    public List<ServicoDTO> listarTodos() {
        return servicoRepository.findAll().stream().map(ServicoDTO::new).collect(Collectors.toList());
    }

    public ServicoDTO buscarDTOPorId(Long id) {
        return servicoRepository.findById(id).map(ServicoDTO::new)
               .orElseThrow(() -> new EntityNotFoundException("Serviço com ID " + id + " não encontrado."));
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
               .orElseThrow(() -> new EntityNotFoundException("Serviço com ID " + id + " não encontrado."));
    }

    public ServicoDTO atualizarServico(Long id, Servico servicoAtualizado) {
        Servico servicoExistente = buscarPorId(id);
        Optional<Servico> conflitoNome = servicoRepository.findByNomeIgnoreCase(servicoAtualizado.getNome());
        if (conflitoNome.isPresent() && !conflitoNome.get().getIdServico().equals(id)) {
             throw new IllegalArgumentException("Erro: Nome do serviço já pertence a outro registro.");
        }
        if (servicoAtualizado.getValorUnitario() == null || servicoAtualizado.getValorUnitario().doubleValue() < 0) {
             throw new IllegalArgumentException("Erro: Valor unitário obrigatório e não negativo.");
        }
        if (servicoAtualizado.getEstoque() != null && servicoAtualizado.getEstoque() < 0) {
             throw new IllegalArgumentException("Erro: Estoque não pode ser negativo.");
        }
         if (servicoAtualizado.getTempoExecucaoHoras() != null && servicoAtualizado.getTempoExecucaoHoras() < 0) {
              throw new IllegalArgumentException("Erro: Tempo de execução não pode ser negativo.");
         }
        servicoExistente.setNome(servicoAtualizado.getNome());
        servicoExistente.setDescricao(servicoAtualizado.getDescricao());
        servicoExistente.setValorUnitario(servicoAtualizado.getValorUnitario());
        servicoExistente.setTempoExecucaoHoras(servicoAtualizado.getTempoExecucaoHoras());
        servicoExistente.setEstoque(servicoAtualizado.getEstoque());
        Servico salvo = servicoRepository.save(servicoExistente);
        return new ServicoDTO(salvo);
    }

     public void deletarServico(Long id) {
         if (!servicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Serviço com ID " + id + " não encontrado.");
         }
         // Usa o método do ItemOrcamentoRepository real
         if (!itemOrcamentoRepository.findByServicoIdServico(id).isEmpty()) {
             throw new IllegalStateException("Erro: Não é possível excluir o serviço pois ele já foi utilizado em orçamentos.");
         }
         servicoRepository.deleteById(id);
     }

     // <<< REMOVER A INTERFACE INTERNA QUE ESTAVA AQUI >>>
}