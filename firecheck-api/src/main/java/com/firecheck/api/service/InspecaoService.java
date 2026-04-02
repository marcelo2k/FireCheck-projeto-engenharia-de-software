package com.firecheck.api.service;

import com.firecheck.api.dto.InspecaoDTO;
import com.firecheck.api.model.*; // Import all
import com.firecheck.api.repository.*; // Import all
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // Importar
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InspecaoService {

    @Autowired private InspecaoRepository inspecaoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EdificacaoRepository edificacaoRepository;
    @Autowired private ItemInspecionadoRepository itemInspecionadoRepository; // Para checar delete

    public InspecaoDTO agendarInspecao(Inspecao inspecao, Long idTecnico, Long idEdificacao) {
        Usuario tecnico = usuarioRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("Técnico com ID " + idTecnico + " não encontrado."));
        Edificacao edificacao = edificacaoRepository.findById(idEdificacao)
                .orElseThrow(() -> new EntityNotFoundException("Edificação com ID " + idEdificacao + " não encontrada."));
        inspecao.setTecnico(tecnico);
        inspecao.setEdificacao(edificacao);
        // Garante que a data e o status sejam definidos se não vierem no objeto
        if (inspecao.getDataInspecao() == null) inspecao.setDataInspecao(LocalDateTime.now());
        if (inspecao.getStatus() == null) inspecao.setStatus("Agendada");

        Inspecao salva = inspecaoRepository.save(inspecao);
        return new InspecaoDTO(salva);
    }

    public List<InspecaoDTO> listarTodas() {
        return inspecaoRepository.findAll().stream().map(InspecaoDTO::new).collect(Collectors.toList());
    }

     public InspecaoDTO buscarInspecaoDTOPorId(Long id) {
        return inspecaoRepository.findById(id).map(InspecaoDTO::new)
               .orElseThrow(() -> new EntityNotFoundException("Inspeção com ID " + id + " não encontrada."));
    }

    public Inspecao buscarInspecaoPorId(Long id) {
        return inspecaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inspeção com ID " + id + " não encontrada."));
    }

    // --- NOVO MÉTODO: ATUALIZAR (Ex: Status, Data, Tecnico) ---
    public InspecaoDTO atualizarInspecao(Long id, Inspecao inspecaoAtualizada, Long idTecnicoNovo, Long idEdificacaoNova) {
        Inspecao inspExistente = buscarInspecaoPorId(id); // Já lança 404
        Usuario tecnicoNovo = usuarioRepository.findById(idTecnicoNovo)
                .orElseThrow(() -> new EntityNotFoundException("Técnico com ID " + idTecnicoNovo + " não encontrado."));
        Edificacao edificacaoNova = edificacaoRepository.findById(idEdificacaoNova)
                 .orElseThrow(() -> new EntityNotFoundException("Edificação com ID " + idEdificacaoNova + " não encontrada."));

        // Atualiza campos permitidos (ex: não mudar data de criação)
        inspExistente.setDataInspecao(inspecaoAtualizada.getDataInspecao());
        inspExistente.setStatus(inspecaoAtualizada.getStatus());
        inspExistente.setTecnico(tecnicoNovo);
        inspExistente.setEdificacao(edificacaoNova); // Permite reassociar, cuidado com itens já registrados!

        Inspecao salva = inspecaoRepository.save(inspExistente);
        return new InspecaoDTO(salva);
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: DELETAR ---
    public void deletarInspecao(Long id) {
         if (!inspecaoRepository.existsById(id)) {
             throw new EntityNotFoundException("Inspeção com ID " + id + " não encontrada.");
        }
        // Verifica se há itens inspecionados associados
        if (!itemInspecionadoRepository.findByIdIdInspecao(id).isEmpty()) {
            throw new IllegalStateException("Erro: Não é possível excluir a inspeção pois ela possui itens registrados. Exclua os itens primeiro.");
            // Alternativa: Excluir itens em cascata (configuração JPA ou lógica aqui)
        }
        inspecaoRepository.deleteById(id);
    }
    // --- FIM NOVO MÉTODO ---
}