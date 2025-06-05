package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import java.time.LocalDate;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrcamentoRepositorio extends JpaRepository<OrcamentoModel, Long> {
    
    List<OrcamentoModel> findByEfetivadoIsTrueOrderByIdDesc(Pageable pageable);
    List<OrcamentoModel> findByEfetivadoIsTrueAndDataGeracaoBetweenOrderByIdDesc(LocalDate dataInicial, LocalDate dataFinal);

    @Query("SELECT DISTINCT o.cliente.nomeCompleto FROM OrcamentoModel o WHERE o.efetivado = true AND o.cliente IS NOT NULL AND o.cliente.nomeCompleto IS NOT NULL ORDER BY o.cliente.nomeCompleto ASC")
    List<String> findDistinctNomesClientesComOrcamentosEfetivados(); // Nome do método mantido, query atualizada

    @Query("SELECT o FROM OrcamentoModel o WHERE o.cliente.nomeCompleto = :nomeCliente AND o.efetivado = true ORDER BY o.dataGeracao DESC")
    List<OrcamentoModel> findByClienteNomeCompletoAndEfetivadoIsTrueOrderByDataGeracaoDesc(@Param("nomeCliente") String nomeCliente);
    
    @Query("SELECT o FROM OrcamentoModel o WHERE o.cliente.nomeCompleto = :nomeCliente AND o.efetivado = true AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal ORDER BY o.dataGeracao DESC")
    List<OrcamentoModel> findByClienteNomeCompletoAndEfetivadoIsTrueAndDataGeracaoBetweenOrderByDataGeracaoDesc(
            @Param("nomeCliente") String nomeCliente, 
            @Param("dataInicial") LocalDate dataInicial, 
            @Param("dataFinal") LocalDate dataFinal);

    long countByDataGeracaoBetween(LocalDate dataInicial, LocalDate dataFinal);
    long countByEfetivadoIsTrueAndDataGeracaoBetween(LocalDate dataInicial, LocalDate dataFinal);
}