package com.firecheck.api.controller;

import com.firecheck.api.dto.DashboardDTO;
import com.firecheck.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EdificacaoRepository edificacaoRepository;
    @Autowired private EquipamentoRepository equipamentoRepository;
    @Autowired private OrcamentoRepository orcamentoRepository;
    @Autowired private OrdemServicoRepository osRepository;
    @Autowired private ServicoRepository servicoRepository;

    @GetMapping
    public ResponseEntity<DashboardDTO> getResumo() {
        // Faz as contagens no banco
        long totalClientes = clienteRepository.count();
        long totalTecnicos = usuarioRepository.count();
        long totalEdificacoes = edificacaoRepository.count();
        long totalEquipamentos = equipamentoRepository.count();
        
        long orcamentosPendentes = orcamentoRepository.countByStatus("Pendente");
        
        long osAguardando = osRepository.countByStatusServico("Aguardando Execução");
        long osAndamento = osRepository.countByStatusServico("Em Andamento");
        long osEmAberto = osAguardando + osAndamento;

        long alertasEstoque = servicoRepository.countBaixoEstoque();

        // Cria o objeto com os totais
        DashboardDTO dto = new DashboardDTO(totalClientes, totalTecnicos, totalEdificacoes, totalEquipamentos, orcamentosPendentes, osEmAberto, alertasEstoque);
        
        return ResponseEntity.ok(dto);
    }
}