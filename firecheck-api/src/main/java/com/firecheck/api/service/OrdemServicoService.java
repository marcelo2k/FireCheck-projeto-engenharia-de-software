package com.firecheck.api.service;

import com.firecheck.api.dto.OrdemServicoDTO;
import com.firecheck.api.model.*;
import com.firecheck.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    @Autowired private OrdemServicoRepository osRepository;
    @Autowired private OrcamentoRepository orcamentoRepository;
    @Autowired private ItemOrcamentoRepository itemOrcamentoRepository;
    @Autowired private ServicoRepository servicoRepository;

    @Transactional
    public OrdemServicoDTO aprovarOrcamentoEGerarOS(Long idOrcamento, LocalDate dataExecucaoPrevista) {
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
             .orElseThrow(() -> new EntityNotFoundException("Orçamento com ID " + idOrcamento + " não encontrado."));

        if (!"Pendente".equals(orcamento.getStatus())) {
             throw new IllegalArgumentException("Erro: Orçamento ID " + idOrcamento + " já está com status '" + orcamento.getStatus() + "'.");
        }
        
        if (osRepository.findByOrcamentoIdOrcamento(idOrcamento).isPresent()) {
            throw new IllegalArgumentException("Erro: Já existe uma Ordem de Serviço para o Orçamento ID " + idOrcamento + ".");
        }

        orcamento.setStatus("Aprovado");
        orcamentoRepository.save(orcamento);

        OrdemServico novaOS = new OrdemServico();
        novaOS.setOrcamento(orcamento);
        novaOS.setDataExecucaoPrevista(dataExecucaoPrevista);
        novaOS.setDataCriacao(LocalDate.now());
        novaOS.setStatusServico("Aguardando Execução"); // Status inicial

        OrdemServico osSalva = osRepository.save(novaOS);
        return new OrdemServicoDTO(osSalva);
    }

    public List<OrdemServicoDTO> listarTodas() {
        return osRepository.findAll().stream().map(OrdemServicoDTO::new).collect(Collectors.toList());
    }

     public OrdemServico buscarOrdemServicoPorId(Long id) {
         return osRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Serviço com ID " + id + " não encontrada."));
     }

    public OrdemServicoDTO buscarDTOPorId(Long id) {
        return new OrdemServicoDTO(buscarOrdemServicoPorId(id));
    }

    // --- ATUALIZAR STATUS DA OS (COM BAIXA NO ESTOQUE) ---
    @Transactional
    public OrdemServicoDTO atualizarStatusOS(Long id, String novoStatus) {
        OrdemServico os = buscarOrdemServicoPorId(id);

        // Validar transições
        if ("Concluído".equals(os.getStatusServico()) || "Cancelado".equals(os.getStatusServico())) {
            throw new IllegalStateException("Erro: OS já está finalizada (" + os.getStatusServico() + "). Não é possível alterar.");
        }

        List<String> statusPermitidos = List.of("Aguardando Execução", "Em Andamento", "Concluído", "Cancelado");
        if (!statusPermitidos.contains(novoStatus)) {
             throw new IllegalArgumentException("Erro: Status inválido. Permitidos: " + statusPermitidos);
        }

        // LÓGICA DE ESTOQUE: Se mudou para "Concluído"
        if ("Concluído".equals(novoStatus)) {
            processarBaixaDeEstoque(os);
        }

        os.setStatusServico(novoStatus);
        OrdemServico salva = osRepository.save(os);
        return new OrdemServicoDTO(salva);
    }

    private void processarBaixaDeEstoque(OrdemServico os) {
        // Busca os itens do orçamento vinculado à OS
        List<ItemOrcamento> itens = itemOrcamentoRepository.findByOrcamentoIdOrcamento(os.getOrcamento().getIdOrcamento());
        
        for (ItemOrcamento item : itens) {
            Servico servico = item.getServico();
            // Verifica se o serviço controla estoque (se estoque não é nulo)
            if (servico.getEstoque() != null) {
                int novaQtd = servico.getEstoque() - item.getQuantidade();
                if (novaQtd < 0) {
                    throw new IllegalStateException("Erro: Estoque insuficiente para o item '" + servico.getNome() + "'. Estoque atual: " + servico.getEstoque() + ", Necessário: " + item.getQuantidade());
                }
                servico.setEstoque(novaQtd);
                servicoRepository.save(servico);
                // Aqui poderia ter uma lógica de alerta se (novaQtd < minimo)
            }
        }
    }

    // --- CANCELAR OS (Estorno de estoque?) ---
    @Transactional
    public OrdemServicoDTO cancelarOS(Long id) {
        OrdemServico os = buscarOrdemServicoPorId(id);

        if ("Concluído".equals(os.getStatusServico()) || "Cancelado".equals(os.getStatusServico())) {
            throw new IllegalStateException("Erro: OS já está finalizada.");
        }

        // Se um dia permitirmos cancelar OS "Concluída", teríamos que devolver o estoque aqui.
        // Por enquanto, só cancela se não foi concluída.

        os.setStatusServico("Cancelado");
        OrdemServico salva = osRepository.save(os);
        return new OrdemServicoDTO(salva);
    }
}