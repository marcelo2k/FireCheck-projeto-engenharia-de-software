package com.firecheck.api.service;

import com.firecheck.api.dto.*;
import com.firecheck.api.model.*;
import com.firecheck.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
// import java.util.Optional; // Remover se não usado diretamente
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    @Autowired private OrcamentoRepository orcamentoRepository;
    @Autowired private ItemOrcamentoRepository itemOrcamentoRepository;
    @Autowired private EdificacaoRepository edificacaoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ServicoRepository servicoRepository;
    @Autowired private OrdemServicoRepository osRepository;

    @Transactional
    public OrcamentoDTO criarOrcamento(OrcamentoRequest request) {
        Edificacao edificacao = edificacaoRepository.findById(request.getIdEdificacao())
                .orElseThrow(() -> new EntityNotFoundException("Edificação com ID " + request.getIdEdificacao() + " não encontrada."));
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + request.getIdUsuario() + " não encontrado."));
        if (request.getItens() == null || request.getItens().isEmpty()) { throw new IllegalArgumentException("Erro: O orçamento deve conter pelo menos um item."); }

        Orcamento orcamento = new Orcamento();
        orcamento.setEdificacao(edificacao); orcamento.setUsuario(usuario);
        orcamento.setDataValidade(request.getDataValidade());
        orcamento.setDataCriacao(LocalDate.now()); orcamento.setStatus("Pendente"); orcamento.setValorTotal(BigDecimal.ZERO);
        Orcamento orcamentoSalvo = orcamentoRepository.save(orcamento);
        BigDecimal valorTotalCalculado = BigDecimal.ZERO;

        for (ItemOrcamentoRequest itemReq : request.getItens()) {
            Servico servico = servicoRepository.findById(itemReq.getIdServico())
                 .orElseThrow(() -> new EntityNotFoundException("Serviço com ID " + itemReq.getIdServico() + " não encontrado."));
            if (itemReq.getQuantidade() <= 0) { throw new IllegalArgumentException("Erro: Quantidade do serviço '" + servico.getNome() + "' deve ser positiva."); }
            ItemOrcamento item = new ItemOrcamento();
            item.setOrcamento(orcamentoSalvo); item.setServico(servico);
            item.setQuantidade(itemReq.getQuantidade()); item.setValorUnitario(servico.getValorUnitario());
            itemOrcamentoRepository.save(item);
            BigDecimal subtotal = servico.getValorUnitario().multiply(new BigDecimal(itemReq.getQuantidade()));
            valorTotalCalculado = valorTotalCalculado.add(subtotal);
        }
        orcamentoSalvo.setValorTotal(valorTotalCalculado);
        Orcamento orcamentoFinal = orcamentoRepository.save(orcamentoSalvo);
        return new OrcamentoDTO(orcamentoFinal);
    }

    public List<OrcamentoDTO> listarTodos() {
        return orcamentoRepository.findAll().stream().map(OrcamentoDTO::new).collect(Collectors.toList());
    }

    public List<ItemOrcamentoDTO> listarItensDoOrcamento(Long idOrcamento) {
        // Valida se o orçamento existe antes de buscar itens
        if (!orcamentoRepository.existsById(idOrcamento)) {
             throw new EntityNotFoundException("Orçamento com ID " + idOrcamento + " não encontrado.");
        }
        return itemOrcamentoRepository.findByOrcamentoIdOrcamento(idOrcamento).stream().map(ItemOrcamentoDTO::new).collect(Collectors.toList());
    }

    public Orcamento buscarOrcamentoPorId(Long id) {
         return orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento com ID " + id + " não encontrado."));
    }

     public OrcamentoDTO buscarOrcamentoDTOPorId(Long id) {
          return new OrcamentoDTO(buscarOrcamentoPorId(id));
     }

    @Transactional
    public OrcamentoDTO atualizarStatusOrcamento(Long id, String novoStatus) {
        Orcamento orcamento = buscarOrcamentoPorId(id);
        if (!"Pendente".equals(orcamento.getStatus())) { throw new IllegalStateException("Erro: Orçamento não está mais pendente."); }
        if (!"Aprovado".equals(novoStatus) && !"Recusado".equals(novoStatus)) { throw new IllegalArgumentException("Erro: Novo status inválido."); }
        if ("Aprovado".equals(novoStatus)) { throw new IllegalArgumentException("Erro: Para aprovar, use o endpoint de gerar OS."); }
        orcamento.setStatus(novoStatus);
        Orcamento salvo = orcamentoRepository.save(orcamento);
        return new OrcamentoDTO(salvo);
    }

    @Transactional
    public void deletarOrcamento(Long id) {
         Orcamento orcamento = buscarOrcamentoPorId(id);
         if (!"Pendente".equals(orcamento.getStatus())) { throw new IllegalStateException("Erro: Só pode excluir orçamentos pendentes."); }
         if (osRepository.findByOrcamentoIdOrcamento(id).isPresent()) { throw new IllegalStateException("Erro: Orçamento já gerou OS."); }
         List<ItemOrcamento> itens = itemOrcamentoRepository.findByOrcamentoIdOrcamento(id);
         itemOrcamentoRepository.deleteAll(itens);
         orcamentoRepository.deleteById(id);
    }
}