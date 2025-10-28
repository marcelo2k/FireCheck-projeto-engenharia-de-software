package com.firecheck.api.service;

import com.firecheck.api.dto.OrdemServicoDTO;
import com.firecheck.api.model.Orcamento;
import com.firecheck.api.model.OrdemServico;
import com.firecheck.api.repository.OrcamentoRepository;
import com.firecheck.api.repository.OrdemServicoRepository;
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

    @Transactional
    public OrdemServicoDTO aprovarOrcamentoEGerarOS(Long idOrcamento, LocalDate dataExecucaoPrevista) { /* ... (igual antes, mas retorna DTO) ... */
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
             .orElseThrow(() -> new EntityNotFoundException("Orçamento com ID " + idOrcamento + " não encontrado."));
        if (!"Pendente".equals(orcamento.getStatus())) { throw new IllegalArgumentException("Erro: Orçamento não está pendente."); }
        if (osRepository.findByOrcamentoIdOrcamento(idOrcamento).isPresent()) { throw new IllegalArgumentException("Erro: OS já existe para este orçamento."); }
        orcamento.setStatus("Aprovado"); orcamentoRepository.save(orcamento);
        OrdemServico novaOS = new OrdemServico(); novaOS.setOrcamento(orcamento); novaOS.setDataExecucaoPrevista(dataExecucaoPrevista);
        novaOS.setDataCriacao(LocalDate.now()); novaOS.setStatusServico("Aguardando Execução");
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

    // --- NOVO MÉTODO: ATUALIZAR STATUS DA OS ---
    @Transactional
    public OrdemServicoDTO atualizarStatusOS(Long id, String novoStatus) {
        OrdemServico os = buscarOrdemServicoPorId(id); // Já lança 404

        // Validar transições de status (exemplo simples)
        if ("Concluído".equals(os.getStatusServico()) || "Cancelado".equals(os.getStatusServico())) {
            throw new IllegalStateException("Erro: OS já está Concluída ou Cancelada.");
        }
        // Adicionar mais validações se necessário (ex: só pode ir para 'Em Andamento' se estiver 'Aguardando')

        // Validar os valores permitidos para novoStatus
        List<String> statusPermitidos = List.of("Aguardando Execução", "Em Andamento", "Concluído", "Cancelado");
        if (!statusPermitidos.contains(novoStatus)) {
             throw new IllegalArgumentException("Erro: Status inválido. Permitidos: " + statusPermitidos);
        }

        os.setStatusServico(novoStatus);
        OrdemServico salva = osRepository.save(os);
        return new OrdemServicoDTO(salva);
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: CANCELAR/DELETAR OS (apenas muda status?) ---
    // Geralmente não se deleta uma OS, apenas cancela.
    @Transactional
    public OrdemServicoDTO cancelarOS(Long id) {
        OrdemServico os = buscarOrdemServicoPorId(id);

        if ("Concluído".equals(os.getStatusServico()) || "Cancelado".equals(os.getStatusServico())) {
            throw new IllegalStateException("Erro: OS já está Concluída ou Cancelada.");
        }

        // Reverte o status do Orçamento associado para Pendente? (Opcional, depende da regra de negócio)
        // Orcamento orcamento = os.getOrcamento();
        // if (orcamento != null && "Aprovado".equals(orcamento.getStatus())) {
        //     orcamento.setStatus("Pendente");
        //     orcamentoRepository.save(orcamento);
        // }

        os.setStatusServico("Cancelado");
        OrdemServico salva = osRepository.save(os);
        return new OrdemServicoDTO(salva);
    }
    // --- FIM NOVO MÉTODO ---
}