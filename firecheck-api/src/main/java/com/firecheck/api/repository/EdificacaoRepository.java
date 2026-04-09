package com.firecheck.api.repository;

import com.firecheck.api.model.Edificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdificacaoRepository extends JpaRepository<Edificacao, Long> {

    List<Edificacao> findByClienteId(Long idCliente);

}