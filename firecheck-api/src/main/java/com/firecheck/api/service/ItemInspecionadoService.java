package com.firecheck.api.service;

import com.firecheck.api.dto.ItemInspecionadoDTO;
import com.firecheck.api.model.*;
import com.firecheck.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemInspecionadoService {

    @Autowired private ItemInspecionadoRepository itemRepository;
    @Autowired private InspecaoRepository inspecaoRepository;
    @Autowired private EquipamentoRepository equipamentoRepository;
    @Autowired private NaoConformidadeRepository naoConformidadeRepository; // Injeção correta

    public ItemInspecionadoDTO salvarItem(ItemInspecionado item, Long idInspecao, Long idEquipamento) {
        Inspecao inspecao = inspecaoRepository.findById(idInspecao)
                .orElseThrow(() -> new EntityNotFoundException("Inspeção com ID " + idInspecao + " não encontrada."));
        Equipamento equipamento = equipamentoRepository.findById(idEquipamento)
                .orElseThrow(() -> new EntityNotFoundException("Equipamento com ID " + idEquipamento + " não encontrado."));
        if (!equipamento.getEdificacao().getIdEdificacao().equals(inspecao.getEdificacao().getIdEdificacao())) {
            throw new IllegalArgumentException("Erro: Equipamento não pertence à edificação da Inspeção.");
        }
        ItemInspecionadoId itemId = new ItemInspecionadoId(idInspecao, idEquipamento);
        item.setId(itemId);
        item.setInspecao(inspecao);
        item.setEquipamento(equipamento);
        ItemInspecionado salvo = itemRepository.save(item);
        return new ItemInspecionadoDTO(salvo);
    }

    public List<ItemInspecionadoDTO> listarPorInspecao(Long idInspecao) {
        return itemRepository.findByIdIdInspecao(idInspecao).stream().map(ItemInspecionadoDTO::new).collect(Collectors.toList());
    }

    public ItemInspecionado buscarItemPorPk(Long idInspecao, Long idEquipamento) {
        ItemInspecionadoId id = new ItemInspecionadoId(idInspecao, idEquipamento);
        return itemRepository.findById(id)
               .orElseThrow(() -> new EntityNotFoundException("Item Inspecionado não encontrado para Inspecao ID " + idInspecao + " e Equipamento ID " + idEquipamento));
    }

     public ItemInspecionadoDTO buscarItemDTOPorPk(Long idInspecao, Long idEquipamento) {
        return new ItemInspecionadoDTO(buscarItemPorPk(idInspecao, idEquipamento));
    }

     public ItemInspecionadoDTO atualizarItem(Long idInspecao, Long idEquipamento, ItemInspecionado itemAtualizado) {
         ItemInspecionado itemExistente = buscarItemPorPk(idInspecao, idEquipamento);
         itemExistente.setStatusGeral(itemAtualizado.getStatusGeral());
         itemExistente.setObservacoes(itemAtualizado.getObservacoes());
         ItemInspecionado salvo = itemRepository.save(itemExistente);
         return new ItemInspecionadoDTO(salvo);
     }

    public void deletarItem(Long idInspecao, Long idEquipamento) {
        ItemInspecionadoId id = new ItemInspecionadoId(idInspecao, idEquipamento);
        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("Item Inspecionado não encontrado para Inspecao ID " + idInspecao + " e Equipamento ID " + idEquipamento);
        }
        // Usa o método do NaoConformidadeRepository real
         if (!naoConformidadeRepository.findByItemInspecionadoIdIdInspecaoAndItemInspecionadoIdIdEquipamento(idInspecao, idEquipamento).isEmpty()) {
              throw new IllegalStateException("Erro: Não é possível excluir o item pois ele possui Não Conformidades registradas.");
         }
        itemRepository.deleteById(id);
    }

    // <<< REMOVER A INTERFACE INTERNA QUE ESTAVA AQUI >>>
}