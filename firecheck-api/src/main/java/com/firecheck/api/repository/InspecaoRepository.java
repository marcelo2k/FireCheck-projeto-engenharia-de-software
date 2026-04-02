package com.firecheck.api.repository;

import com.firecheck.api.model.Inspecao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InspecaoRepository extends JpaRepository<Inspecao, Long> {
    List<Inspecao> findByTecnicoId(Long idTecnico);
    List<Inspecao> findByEdificacaoIdEdificacao(Long idEdificacao);
}