package com.firecheck.api.repository;

import com.firecheck.api.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    List<Orcamento> findByStatus(String status); // Ex: buscar pendentes
    List<Orcamento> findByEdificacaoIdEdificacao(Long idEdificacao);
}