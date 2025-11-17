package com.firecheck.api.repository;

import com.firecheck.api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    Optional<Servico> findByNomeIgnoreCase(String nome); // Busca por nome ignorando maiúsculas/minúsculas
}