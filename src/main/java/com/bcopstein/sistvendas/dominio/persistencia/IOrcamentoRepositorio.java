package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import java.time.LocalDate;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query; // Adicionar import
//import org.springframework.data.repository.query.Param; // Adicionar import
import org.springframework.stereotype.Repository;

@Repository
public interface IOrcamentoRepositorio extends JpaRepository<OrcamentoModel, Long> {
    // ... (métodos existentes) ...
    List<OrcamentoModel> findByEfetivadoIsTrueAndDataGeracaoBetweenOrderByIdDesc(LocalDate dataInicial, LocalDate dataFinal);
    List<OrcamentoModel> findByNomeClienteAndEfetivadoIsTrueAndDataGeracaoBetweenOrderByDataGeracaoDesc(
            String nomeCliente, LocalDate dataInicial, LocalDate dataFinal);
    List<OrcamentoModel> findByNomeClienteAndEfetivadoIsTrue(String nomeCliente);
        List<OrcamentoModel> findByNomeClienteAndEfetivadoIsTrueOrderByDataGeracaoDesc(String nomeCliente);



    // >>> NOVOS MÉTODOS para contagem <<<
    long countByDataGeracaoBetween(LocalDate dataInicial, LocalDate dataFinal);

    long countByEfetivadoIsTrueAndDataGeracaoBetween(LocalDate dataInicial, LocalDate dataFinal);
}