package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrcamentoRepositorio extends JpaRepository<OrcamentoModel, Long> {
    // Encontra os 'n' últimos orçamentos efetivados, ordenados por ID descendente
    List<OrcamentoModel> findByEfetivadoIsTrueOrderByIdDesc(Pageable pageable);
}