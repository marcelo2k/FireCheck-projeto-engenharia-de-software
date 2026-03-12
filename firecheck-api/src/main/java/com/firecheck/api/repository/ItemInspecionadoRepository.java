package com.firecheck.api.repository;

import com.firecheck.api.model.ItemInspecionado;
import com.firecheck.api.model.ItemInspecionadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemInspecionadoRepository extends JpaRepository<ItemInspecionado, ItemInspecionadoId> {
    List<ItemInspecionado> findByIdIdInspecao(Long idInspecao);
    List<ItemInspecionado> findByIdIdEquipamento(Long idEquipamento);
}