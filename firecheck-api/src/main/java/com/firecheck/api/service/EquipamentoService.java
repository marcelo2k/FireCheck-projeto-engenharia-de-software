package com.firecheck.api.service;

import com.firecheck.api.dto.EquipamentoDTO;
import com.firecheck.api.model.Edificacao;
import com.firecheck.api.model.Equipamento;
import com.firecheck.api.repository.EdificacaoRepository;
import com.firecheck.api.repository.EquipamentoRepository;
import com.firecheck.api.repository.ItemInspecionadoRepository; // Para checar delete
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipamentoService {

    @Autowired private EquipamentoRepository equipamentoRepository;
    @Autowired private EdificacaoRepository edificacaoRepository;
    @Autowired private ItemInspecionadoRepository itemInspecionadoRepository; // Para checar delete

    public EquipamentoDTO cadastrarEquipamento(Equipamento equipamento, Long idEdificacao) {
        Edificacao edificacao = edificacaoRepository.findById(idEdificacao)
                .orElseThrow(() -> new EntityNotFoundException("Edificação com ID " + idEdificacao + " não encontrada."));
        equipamento.setEdificacao(edificacao);
        Equipamento salvo = equipamentoRepository.save(equipamento);
        return new EquipamentoDTO(salvo);
    }

    public List<EquipamentoDTO> listarTodos() {
        return equipamentoRepository.findAll().stream().map(EquipamentoDTO::new).collect(Collectors.toList());
    }

    public List<EquipamentoDTO> listarPorEdificacao(Long idEdificacao) {
        return equipamentoRepository.findByEdificacaoIdEdificacao(idEdificacao).stream().map(EquipamentoDTO::new).collect(Collectors.toList());
    }

    public EquipamentoDTO buscarEquipamentoDTOPorId(Long id) {
        return equipamentoRepository.findById(id).map(EquipamentoDTO::new)
               .orElseThrow(() -> new EntityNotFoundException("Equipamento com ID " + id + " não encontrado."));
    }

    public Equipamento buscarEquipamentoPorId(Long id) {
         return equipamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipamento com ID " + id + " não encontrado."));
    }

    // --- NOVO MÉTODO: ATUALIZAR ---
    public EquipamentoDTO atualizarEquipamento(Long id, Equipamento equipamentoAtualizado, Long idEdificacaoNova) {
        Equipamento eqExistente = buscarEquipamentoPorId(id); // Já lança 404
        Edificacao edificacaoNova = edificacaoRepository.findById(idEdificacaoNova)
                .orElseThrow(() -> new EntityNotFoundException("Edificação com ID " + idEdificacaoNova + " não encontrada."));

        eqExistente.setTipoEquipamento(equipamentoAtualizado.getTipoEquipamento());
        eqExistente.setLocalizacao(equipamentoAtualizado.getLocalizacao());
        eqExistente.setDataFabricacao(equipamentoAtualizado.getDataFabricacao());
        eqExistente.setDataValidade(equipamentoAtualizado.getDataValidade());
        eqExistente.setEdificacao(edificacaoNova); // Atualiza a edificação associada

        Equipamento salvo = equipamentoRepository.save(eqExistente);
        return new EquipamentoDTO(salvo);
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: DELETAR ---
    public void deletarEquipamento(Long id) {
        if (!equipamentoRepository.existsById(id)) {
             throw new EntityNotFoundException("Equipamento com ID " + id + " não encontrado.");
        }
        // Verifica se há itens inspecionados associados (requer repo JPA)
        if (!itemInspecionadoRepository.findByIdIdEquipamento(id).isEmpty()) {
            throw new IllegalStateException("Erro: Não é possível excluir o equipamento pois ele possui registros em inspeções.");
        }
        // Adicionar outras checagens se necessário (NaoConformidade?)

        equipamentoRepository.deleteById(id);
    }
    // --- FIM NOVO MÉTODO ---
}