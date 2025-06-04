package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import java.time.LocalDate; // Adicionar import
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrcamentoRepositorio extends JpaRepository<OrcamentoModel, Long> {
    // Método existente para os 'n' últimos orçamentos efetivados
    List<OrcamentoModel> findByEfetivadoIsTrueOrderByIdDesc(Pageable pageable); //

    // >>> NOVO MÉTODO para buscar por período <<<
    // Busca orçamentos efetivados cuja dataGeracao esteja entre dataInicial e dataFinal (inclusivo).
    List<OrcamentoModel> findByEfetivadoIsTrueAndDataGeracaoBetweenOrderByIdDesc(LocalDate dataInicial, LocalDate dataFinal);
}