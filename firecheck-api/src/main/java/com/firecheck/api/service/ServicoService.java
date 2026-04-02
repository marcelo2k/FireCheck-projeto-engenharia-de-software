package com.firecheck.api.service;

import com.firecheck.api.dto.ServicoDTO;
import com.firecheck.api.model.Servico;
import com.firecheck.api.repository.ItemOrcamentoRepository;
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
    @Autowired private ItemOrcamentoRepository itemOrcamentoRepository;

    public ServicoDTO cadastrarServico(Servico servico) {
        Optional<Servico> existente = servicoRepository.findByNomeIgnoreCase(servico.getNome());
        if (existente.isPresent()) throw new IllegalArgumentException("Já existe um serviço com este nome.");
        if (servico.getValorUnitario() == null || servico.getValorUnitario().doubleValue() < 0) throw new IllegalArgumentException("Valor unitário inválido.");
        
        // Validações opcionais
        if (servico.getEstoque() != null && servico.getEstoque() < 0) throw new IllegalArgumentException("Estoque não pode ser negativo.");
        if (servico.getEstoqueMinimo() != null && servico.getEstoqueMinimo() < 0) throw new IllegalArgumentException("Estoque mínimo não pode ser negativo.");

        Servico salvo = servicoRepository.save(servico);
        return new ServicoDTO(salvo);
    }

    public List<ServicoDTO> listarTodos() {
        return servicoRepository.findAll().stream().map(ServicoDTO::new).collect(Collectors.toList());
    }

    // --- NOVO: Listar Alertas ---
    public List<ServicoDTO> listarBaixoEstoque() {
        return servicoRepository.findServicosComBaixoEstoque().stream().map(ServicoDTO::new).collect(Collectors.toList());
    }

    public ServicoDTO buscarDTOPorId(Long id) {
        return servicoRepository.findById(id).map(ServicoDTO::new)
               .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado."));
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado."));
    }

    public ServicoDTO atualizarServico(Long id, Servico servicoAtualizado) {
        Servico servicoExistente = buscarPorId(id);
        Optional<Servico> conflitoNome = servicoRepository.findByNomeIgnoreCase(servicoAtualizado.getNome());
        if (conflitoNome.isPresent() && !conflitoNome.get().getIdServico().equals(id)) throw new IllegalArgumentException("Nome já em uso.");
        
        servicoExistente.setNome(servicoAtualizado.getNome());
        servicoExistente.setDescricao(servicoAtualizado.getDescricao());
        servicoExistente.setValorUnitario(servicoAtualizado.getValorUnitario());
        servicoExistente.setTempoExecucaoHoras(servicoAtualizado.getTempoExecucaoHoras());
        servicoExistente.setEstoque(servicoAtualizado.getEstoque());
        servicoExistente.setEstoqueMinimo(servicoAtualizado.getEstoqueMinimo()); // Atualiza mínimo
        
        Servico salvo = servicoRepository.save(servicoExistente);
        return new ServicoDTO(salvo);
    }

     public void deletarServico(Long id) {
         if (!servicoRepository.existsById(id)) throw new EntityNotFoundException("Serviço não encontrado.");
         if (!itemOrcamentoRepository.findByServicoIdServico(id).isEmpty()) throw new IllegalStateException("Serviço em uso.");
         servicoRepository.deleteById(id);
     }
}