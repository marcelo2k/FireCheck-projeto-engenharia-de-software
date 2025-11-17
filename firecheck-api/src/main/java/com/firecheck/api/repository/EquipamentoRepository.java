package com.firecheck.api.repository;

import com.firecheck.api.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    List<Equipamento> findByEdificacaoIdEdificacao(Long idEdificacao); // Busca por ID da edificação
}