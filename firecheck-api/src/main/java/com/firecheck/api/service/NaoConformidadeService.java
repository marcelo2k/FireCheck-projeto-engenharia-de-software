package com.firecheck.api.service;

import com.firecheck.api.dto.NaoConformidadeDTO;
import com.firecheck.api.model.ItemInspecionado;
import com.firecheck.api.model.ItemInspecionadoId;
import com.firecheck.api.model.NaoConformidade;
import com.firecheck.api.repository.ItemInspecionadoRepository;
import com.firecheck.api.repository.NaoConformidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NaoConformidadeService {

    @Autowired private NaoConformidadeRepository naoConformidadeRepository;
    @Autowired private ItemInspecionadoRepository itemInspecionadoRepository;

    public NaoConformidadeDTO registrarNaoConformidade(NaoConformidade nc, Long idInspecao, Long idEquipamento) {
        ItemInspecionadoId itemId = new ItemInspecionadoId(idInspecao, idEquipamento);
        ItemInspecionado item = itemInspecionadoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item Inspecionado não encontrado para IDs fornecidos."));
        // if (!"Não Conforme".equals(item.getStatusGeral())) { /* Aviso opcional */ }
        nc.setItemInspecionado(item);
        NaoConformidade salva = naoConformidadeRepository.save(nc);
        return new NaoConformidadeDTO(salva);
    }

    public List<NaoConformidadeDTO> listarPorInspecao(Long idInspecao) {
        return naoConformidadeRepository.findByItemInspecionadoIdIdInspecao(idInspecao).stream().map(NaoConformidadeDTO::new).collect(Collectors.toList());
    }

     public NaoConformidadeDTO buscarNCDTOPorId(Long id) {
        return naoConformidadeRepository.findById(id).map(NaoConformidadeDTO::new)
               .orElseThrow(() -> new EntityNotFoundException("Não Conformidade com ID " + id + " não encontrada."));
    }

    // --- NOVO MÉTODO: ATUALIZAR ---
    public NaoConformidadeDTO atualizarNaoConformidade(Long id, NaoConformidade ncAtualizada) {
         NaoConformidade ncExistente = naoConformidadeRepository.findById(id)
               .orElseThrow(() -> new EntityNotFoundException("Não Conformidade com ID " + id + " não encontrada."));

         // Atualiza apenas descrição e foto (não muda o item associado)
         ncExistente.setDescricao(ncAtualizada.getDescricao());
         ncExistente.setFotoUrl(ncAtualizada.getFotoUrl());

         NaoConformidade salva = naoConformidadeRepository.save(ncExistente);
         return new NaoConformidadeDTO(salva);
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: DELETAR ---
    public void deletarNaoConformidade(Long id) {
         if (!naoConformidadeRepository.existsById(id)) {
            throw new EntityNotFoundException("Não Conformidade com ID " + id + " não encontrada.");
         }
         // Nenhuma dependência óbvia para checar
         naoConformidadeRepository.deleteById(id);
    }
    // --- FIM NOVO MÉTODO ---
}