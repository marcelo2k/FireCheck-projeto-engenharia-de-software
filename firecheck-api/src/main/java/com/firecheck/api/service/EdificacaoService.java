package com.firecheck.api.service;

import com.firecheck.api.dto.EdificacaoDTO;
import com.firecheck.api.model.Cliente;
import com.firecheck.api.model.Edificacao;
import com.firecheck.api.repository.ClienteRepository;
import com.firecheck.api.repository.EdificacaoRepository;
import com.firecheck.api.repository.EquipamentoRepository; // Importar para checar dependência
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EdificacaoService {

    @Autowired
    private EdificacaoRepository edificacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired // Injetar para verificar dependências
    private EquipamentoRepository equipamentoRepository; // Assumindo que já é JpaRepository

    public EdificacaoDTO cadastrarEdificacao(Edificacao edificacao, Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + idCliente + " não encontrado."));
        edificacao.setCliente(cliente);
        Edificacao salva = edificacaoRepository.save(edificacao);
        return new EdificacaoDTO(salva); // Retorna DTO
    }

    public List<EdificacaoDTO> listarTodas() {
        return edificacaoRepository.findAll().stream()
                           .map(EdificacaoDTO::new)
                           .collect(Collectors.toList());
    }

    public List<EdificacaoDTO> listarPorIdCliente(Long idCliente) {
        return edificacaoRepository.findByClienteId(idCliente).stream()
                           .map(EdificacaoDTO::new)
                           .collect(Collectors.toList());
    }

    // Método para buscar a entidade (usado internamente e para update)
    public Edificacao buscarEdificacaoPorId(Long id) {
        return edificacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Edificação com ID " + id + " não encontrada."));
    }

     // Método para buscar o DTO (usado pelo controller no GET por ID)
     public EdificacaoDTO buscarEdificacaoDTOPorId(Long id) {
          Edificacao edificacao = buscarEdificacaoPorId(id); // Reusa o método acima
          return new EdificacaoDTO(edificacao);
     }


    // --- NOVO MÉTODO: ATUALIZAR ---
    public EdificacaoDTO atualizarEdificacao(Long id, Edificacao edificacaoAtualizada, Long idClienteNovo) {
        // 1. Busca a edificação existente (lança 404 se não achar)
        Edificacao edificacaoExistente = buscarEdificacaoPorId(id);

        // 2. Busca e valida o novo Cliente
        Cliente clienteNovo = clienteRepository.findById(idClienteNovo)
                 .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + idClienteNovo + " não encontrado para associar."));

        // 3. Atualiza os dados
        edificacaoExistente.setNome(edificacaoAtualizada.getNome());
        edificacaoExistente.setEndereco(edificacaoAtualizada.getEndereco());
        edificacaoExistente.setCep(edificacaoAtualizada.getCep());
        edificacaoExistente.setCliente(clienteNovo); // Associa o novo cliente

        // 4. Salva (Update)
        Edificacao salva = edificacaoRepository.save(edificacaoExistente);
        return new EdificacaoDTO(salva); // Retorna DTO atualizado
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: DELETAR ---
    public void deletarEdificacao(Long id) {
        // 1. Verifica se existe (lança 404 se não)
        if (!edificacaoRepository.existsById(id)) {
             throw new EntityNotFoundException("Edificação com ID " + id + " não encontrada.");
        }

        // 2. Verifica dependências (Equipamentos)
        //    Assume que EquipamentoRepository é JpaRepository e tem findByEdificacaoIdEdificacao
        if (!equipamentoRepository.findByEdificacaoIdEdificacao(id).isEmpty()) {
            throw new IllegalStateException("Erro: Não é possível excluir a edificação pois ela possui equipamentos associados.");
        }
        // Adicionar outras verificações aqui se necessário (ex: Inspeções, Orçamentos)

        // 3. Deleta
        edificacaoRepository.deleteById(id);
    }
    // --- FIM NOVO MÉTODO ---
}