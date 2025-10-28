package com.firecheck.api.repository;

import com.firecheck.api.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    Optional<OrdemServico> findByOrcamentoIdOrcamento(Long idOrcamento); // Busca por ID do orçamento associado
}