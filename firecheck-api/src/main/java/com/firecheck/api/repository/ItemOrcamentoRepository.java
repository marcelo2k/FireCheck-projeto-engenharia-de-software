package com.firecheck.api.repository;

import com.firecheck.api.model.ItemOrcamento; // Certifique-se que o import existe
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemOrcamentoRepository extends JpaRepository<ItemOrcamento, Long> {

    List<ItemOrcamento> findByOrcamentoIdOrcamento(Long idOrcamento);

    // --- MÉTODO ADICIONADO ---
    // Busca por ID do serviço associado
    List<ItemOrcamento> findByServicoIdServico(Long idServico);
    // --- FIM MÉTODO ADICIONADO ---
}