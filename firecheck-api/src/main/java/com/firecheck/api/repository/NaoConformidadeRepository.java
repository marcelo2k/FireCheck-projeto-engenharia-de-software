package com.firecheck.api.repository;

import com.firecheck.api.model.NaoConformidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NaoConformidadeRepository extends JpaRepository<NaoConformidade, Long> {

    List<NaoConformidade> findByItemInspecionadoIdIdInspecao(Long idInspecao);

    List<NaoConformidade> findByItemInspecionadoIdIdEquipamento(Long idEquipamento);

    // --- MÉTODO ADICIONADO ---
    // Busca pela chave composta completa do ItemInspecionado associado
    List<NaoConformidade> findByItemInspecionadoIdIdInspecaoAndItemInspecionadoIdIdEquipamento(Long idInspecao, Long idEquipamento);
    // --- FIM MÉTODO ADICIONADO ---
}