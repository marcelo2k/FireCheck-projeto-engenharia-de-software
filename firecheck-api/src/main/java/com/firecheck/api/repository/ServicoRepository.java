package com.firecheck.api.repository;

import com.firecheck.api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    Optional<Servico> findByNomeIgnoreCase(String nome);

    @Query("SELECT s FROM Servico s WHERE s.estoque <= s.estoqueMinimo")
    List<Servico> findServicosComBaixoEstoque();

    // NOVO: Contar quantos serviços estão com estoque baixo
    @Query("SELECT COUNT(s) FROM Servico s WHERE s.estoque <= s.estoqueMinimo")
    long countBaixoEstoque();
}